package site.siredvin.cloudsolutions.computercraft.peripheral

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.resources.ResourceLocation
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.SubscriptionManager
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import site.siredvin.cloudsolutions.util.toListTag
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import java.time.Instant
import java.util.Optional
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull
import kotlin.toString

class KVStoragePeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "kv_storage"
        val ID = ResourceLocation(CloudSolutionsCore.MOD_ID, TYPE)
        val REGEX_CACHE = CacheBuilder.newBuilder().maximumSize(1_000).expireAfterAccess(30, TimeUnit.MINUTES).build(
            CacheLoader.from { it: String -> Regex(it) },
        )
        const val KV_STORAGE_CHANGED_SUBS = "kv_storage_changed_subs"
        const val KV_STORAGE_DELETED_SUBS = "kv_storage_changed_subs"
        const val KV_STORAGE_KEY_CHANGED = "kv_storage_key_changed"
        const val KV_STORAGE_KEY_DELETED = "kv_storage_key_deleted"
    }

    private val changedSubscriptions: MutableMap<String, MutableSet<Int>> = mutableMapOf()
    private val deletedSubscriptions: MutableMap<String, MutableSet<Int>> = mutableMapOf()

    init {
        this.loadSettings()
        val ownerUUID = this.peripheralOwner.ownerUUID?.toString()
        if (ownerUUID != null) {
            SubscriptionManager.addKVStorage(ownerUUID, this)
        }
    }

    override val isEnabled: Boolean
        get() = ModConfig.enableKVStorage

    override val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data = super.peripheralConfiguration
            data["keyLimit"] = ModConfig.kvStorageKeyLimit
            data["valueLimit"] = ModConfig.kvStorageValueLimit
            return data
        }

    @LuaFunction
    fun put(key: String, value: String, expire: Optional<Long>): MethodResult {
        if (value.length > ModConfig.kvStorageValueLimit) throw LuaException("Value is too long")
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager!!.put(player.toString(), key, value, expire.map { Instant.ofEpochSecond(it) }.getOrNull())
    }

    @LuaFunction
    fun delete(key: String): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager!!.delete(player.toString(), key)
    }

    @LuaFunction
    fun get(key: String): String? {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.get(player.toString(), key)
    }

    @LuaFunction
    fun mget(keys: Map<*, *>): Map<String, String> {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.mget(player.toString(), keys.values.map { it.toString() }.toList()) ?: emptyMap()
    }

    @LuaFunction
    fun mput(values: Map<*, *>): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        val transformedMap = values.mapNotNull { entry -> Pair(entry.key.toString(), entry.value.toString()) }.toMap()
        return SubsystemManager.kvManager!!.mput(player.toString(), transformedMap)
    }

    @LuaFunction("get_ex")
    fun getEx(key: String): Long? {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.getExpire(player.toString(), key)?.epochSecond
    }

    @LuaFunction("put_ex")
    fun putEx(key: String, expire: Optional<Long>): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager!!.putExpire(player.toString(), key, expire.map { Instant.ofEpochSecond(it) }.getOrNull())
    }

    @LuaFunction
    fun list(glob: Optional<String>): List<String> {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.list(player.toString(), glob) ?: emptyList()
    }

    @LuaFunction
    fun incr(key: String, value: Optional<Double>): Double {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.incr(player.toString(), key, value.orElse(1.0)) ?: 0.0
    }

    @LuaFunction
    fun decr(key: String, value: Optional<Double>): Double {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.incr(player.toString(), key, -value.orElse(1.0)) ?: 0.0
    }

    @LuaFunction(mainThread = true)
    fun getSubscriptions(access: IComputerAccess, type: String): List<String> = when (type) {
        "changed" -> listSubscriptions(changedSubscriptions, access.id)
        "deleted" -> listSubscriptions(deletedSubscriptions, access.id)
        else -> throw LuaException("You can only list subscriptions to changed or deleted events")
    }

    @LuaFunction(mainThread = true)
    fun subscribe(access: IComputerAccess, type: String, pattern: String) {
        when (type) {
            "changed" -> addSubscription(changedSubscriptions, pattern, access.id)
            "deleted" -> addSubscription(deletedSubscriptions, pattern, access.id)
            else -> throw LuaException("You can subscribe only to changed or deleted events")
        }
    }

    @LuaFunction(mainThread = true)
    fun unsubscribe(access: IComputerAccess, type: String, pattern: String) {
        when (type) {
            "changed" -> removeSubscription(changedSubscriptions, pattern, access.id)
            "deleted" -> removeSubscription(deletedSubscriptions, pattern, access.id)
            else -> throw LuaException("You can unsubscribe only from changed or deleted events")
        }
    }

    private fun listSubscriptions(subMap: MutableMap<String, MutableSet<Int>>, computerID: Int): List<String> = subMap.filter { it.value.contains(computerID) }.map { it.key }

    private fun addSubscription(subMap: MutableMap<String, MutableSet<Int>>, pattern: String, computerID: Int) {
        if (!subMap.contains(pattern)) {
            subMap[pattern] = mutableSetOf()
        }
        if (subMap[pattern]!!.add(computerID)) {
            saveSettings()
        }
    }

    private fun removeSubscription(subMap: MutableMap<String, MutableSet<Int>>, pattern: String, computerID: Int) {
        if (subMap.contains(pattern)) {
            if (subMap[pattern]!!.remove(computerID)) {
                saveSettings()
            }
        }
    }

    fun onKeyChanged(key: String, value: String) {
        changedSubscriptions.forEach {
            if (REGEX_CACHE.get(it.key).matches(key)) {
                this.forEachComputer { computerAccess ->
                    if (it.value.contains(computerAccess.id)) {
                        computerAccess.queueEvent(KV_STORAGE_KEY_CHANGED, key, value)
                    }
                }
            }
        }
    }

    fun onKeyDeleted(key: String) {
        deletedSubscriptions.forEach {
            if (REGEX_CACHE.get(it.key).matches(key)) {
                this.forEachComputer { computerAccess ->
                    if (it.value.contains(computerAccess.id)) {
                        computerAccess.queueEvent(KV_STORAGE_KEY_DELETED, key)
                    }
                }
            }
        }
    }

    fun loadSettings() {
        val dataStorage = peripheralOwner.dataStorage
        if (dataStorage.has(KV_STORAGE_CHANGED_SUBS)) {
            val tag = dataStorage.getCompound(KV_STORAGE_CHANGED_SUBS)
            changedSubscriptions.clear()
            tag.allKeys.forEach { tagKey ->
                changedSubscriptions[tagKey] = tag.getList(tagKey, IntTag.TAG_INT.toInt()).map { (it as IntTag).asInt }.toMutableSet()
            }
        }
        if (dataStorage.has(KV_STORAGE_DELETED_SUBS)) {
            val tag = dataStorage.getCompound(KV_STORAGE_DELETED_SUBS)
            deletedSubscriptions.clear()
            tag.allKeys.forEach { tagKey ->
                deletedSubscriptions[tagKey] = tag.getList(tagKey, IntTag.TAG_INT.toInt()).map { (it as IntTag).asInt }.toMutableSet()
            }
        }
    }

    fun saveSettings() {
        val dataStorage = peripheralOwner.dataStorage
        if (changedSubscriptions.isNotEmpty()) {
            val tag = CompoundTag()
            changedSubscriptions.forEach { tag.put(it.key.toString(), it.value.toListTag()) }
            dataStorage.putCompound(KV_STORAGE_CHANGED_SUBS, tag)
        }
        if (deletedSubscriptions.isNotEmpty()) {
            val tag = CompoundTag()
            deletedSubscriptions.forEach { tag.put(it.key.toString(), it.value.toListTag()) }
            dataStorage.putCompound(KV_STORAGE_DELETED_SUBS, tag)
        }
    }
}
