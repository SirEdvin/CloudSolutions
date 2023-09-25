package site.siredvin.cloudsolutions.subsystems

import net.minecraft.server.MinecraftServer
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.kv.DisabledKVManager
import site.siredvin.cloudsolutions.subsystems.kv.KeyValueManager
import site.siredvin.cloudsolutions.subsystems.kv.sqlite.KVSQLiteManager
import site.siredvin.cloudsolutions.subsystems.statsq.StatsDClient
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

enum class KVStorageMode {
    DISABLED, SQLITE
}

object SubsystemManager {
    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    var kvManager: KeyValueManager? = null

    fun onServerStart(server: MinecraftServer) {
        kvManager = when (ModConfig.kvStorageMode) {
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
