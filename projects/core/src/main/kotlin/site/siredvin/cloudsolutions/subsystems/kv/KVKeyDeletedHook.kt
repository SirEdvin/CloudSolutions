package site.siredvin.cloudsolutions.subsystems.kv

fun interface KVKeyDeletedHook {
    fun handle(ownerUUID: String, key: String)
}
