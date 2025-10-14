package site.siredvin.cloudsolutions.subsystems.kv

import java.util.UUID

fun interface KVKeyChangedHook {
    fun handle(ownerUUID: String, key: String, value: String)
}