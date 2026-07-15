package site.siredvin.cloudsolutions.subsystems

import net.minecraft.server.MinecraftServer
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.subsystems.crafka.CrafkaSQLiteManager
import site.siredvin.cloudsolutions.subsystems.kv.DisabledKVManager
import site.siredvin.cloudsolutions.subsystems.kv.KeyValueManager
import site.siredvin.cloudsolutions.subsystems.kv.sqlite.KVSQLiteManager
import site.siredvin.cloudsolutions.subsystems.statsq.StatsDClient
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

enum class KVStorageMode {
    DISABLED,
    SQLITE,
}

enum class CrafkaStorageMode {
    DISABLED,
    SQLITE,
}

object SubscriptionManager {
    private val kvStorages: MutableMap<String, MutableSet<KVStoragePeripheral>> = mutableMapOf()

    fun addKVStorage(ownerUUID: String, kvStorage: KVStoragePeripheral) {
        if (!kvStorages.contains(ownerUUID)) {
            kvStorages[ownerUUID] = Collections.newSetFromMap(WeakHashMap())
        }
        kvStorages[ownerUUID]!!.add(kvStorage)
    }

    fun onKeyChanged(ownerUUID: String, key: String, value: String) {
        if (kvStorages.contains(ownerUUID)) {
            kvStorages[ownerUUID]!!.forEach {
                it.onKeyChanged(key, value)
            }
        }
    }

    fun onKeyDeleted(ownerUUID: String, key: String) {
        if (kvStorages.contains(ownerUUID)) {
            kvStorages[ownerUUID]!!.forEach {
                it.onKeyDeleted(key)
            }
        }
    }
}

object SubsystemManager {
    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "CloudSolutions subsystem").apply { isDaemon = true }
    }
    var kvManager: KeyValueManager? = null
    var crafkaBrokerManager: CrafkaSQLiteManager? = null

    fun onServerStart(server: MinecraftServer) {
        kvManager = when (ModConfig.kvStorageMode) {
            KVStorageMode.DISABLED -> DisabledKVManager
            KVStorageMode.SQLITE -> KVSQLiteManager
        }
        kvManager?.init(server, executorService)
        kvManager?.setOnKeyDeletedHook(SubscriptionManager::onKeyDeleted)
        kvManager?.setOnKeyChangedHook(SubscriptionManager::onKeyChanged)

        crafkaBrokerManager = when (ModConfig.crafkaStorageMode) {
            CrafkaStorageMode.DISABLED -> null
            CrafkaStorageMode.SQLITE -> CrafkaSQLiteManager
        }
        crafkaBrokerManager?.init(server, executorService)

        if (ModConfig.enableStatsDConnection) {
            StatsDClient.init()
        }
    }

    fun onServerStop(server: MinecraftServer) {
        kvManager?.stop(server, executorService)
        crafkaBrokerManager?.stop(server, executorService)
        StatsDClient.stop()
    }
}
