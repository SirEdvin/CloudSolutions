package site.siredvin.datafortress.subsystems

import net.minecraft.server.MinecraftServer
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.subsystems.kv.DisabledKVManager
import site.siredvin.datafortress.subsystems.kv.KeyValueManager
import site.siredvin.datafortress.subsystems.kv.sqlite.KVSQLiteManager
import site.siredvin.datafortress.subsystems.statsq.StatsDClient
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

enum class KVStorageMode {
    DISABLED, SQLITE
}

object SubsystemManager {
    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    var kvManager: KeyValueManager? = null

    fun onServerStart(server: MinecraftServer) {
        kvManager = when(ModConfig.kvStorageMode) {
            KVStorageMode.DISABLED -> DisabledKVManager
            KVStorageMode.SQLITE -> KVSQLiteManager
        }
        kvManager?.init(server, executorService)
        if (ModConfig.enableStatsDConnection) {
            StatsDClient.init()
        }
    }

    fun onServerStop(server: MinecraftServer) {
        kvManager?.stop(server, executorService)
        StatsDClient.stop()
    }
}