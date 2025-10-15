package site.siredvin.cloudsolutions.subsystems.kv

fun interface KVKeyChangedHook {
    fun handle(ownerUUID: String, key: String, value: String)
}
