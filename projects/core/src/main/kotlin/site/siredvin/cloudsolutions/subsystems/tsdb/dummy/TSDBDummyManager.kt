package site.siredvin.cloudsolutions.subsystems.tsdb.dummy

import site.siredvin.cloudsolutions.subsystems.tsdb.TSDBManager
import site.siredvin.cloudsolutions.subsystems.tsdb.TimeseriesFrame
import site.siredvin.cloudsolutions.subsystems.tsdb.TimeseriesInformation
import java.time.Instant
import java.util.*

object TSDBDummyManager : TSDBManager {
    private const val POINT_COUNT = 300
    override fun init() {
    }

    override fun query(
        ownerUUID: String,
        namePattern: String,
        tags: Map<String, String>,
        from: Instant,
        to: Instant,
    ): List<TimeseriesFrame> {
        return emptyList()
    }

    override fun getOrCreate(ownerUUID: String, name: String, tags: Map<String, String>, retention: Int): UUID = throw NotImplementedError("You shound't use dummy manager for this")

    override fun update(ownerUUID: String, timeseriesId: UUID, retention: Int): Unit = throw NotImplementedError("You shound't use dummy manager for this")

    override fun delete(ownerUUID: String, timeseriesId: UUID): Unit = throw NotImplementedError("You shound't use dummy manager for this")

    override fun list(ownerUUID: String): List<TimeseriesInformation> = throw NotImplementedError("You shound't use dummy manager for this")

    override fun put(ownerUUID: String, timeseriesId: UUID, measurementValue: Double): Unit = throw NotImplementedError("You shound't use dummy manager for this")

    override fun putMany(ownerUUID: String, values: Map<UUID, Double>): Unit = throw NotImplementedError("You shound't use dummy manager for this")
}
