package site.siredvin.cloudsolutions.subsystems.tsdb.dummy

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import site.siredvin.cloudsolutions.subsystems.tsdb.TSDBManager
import site.siredvin.cloudsolutions.subsystems.tsdb.TimeseriesFrame
import site.siredvin.cloudsolutions.subsystems.tsdb.TimeseriesInformation
import java.time.Instant
import java.util.*
import kotlin.random.Random

class MagicPolynom(private val fromEpoch: Long, private val toEpoch: Long) {
    private val elements: List<Long>
    private val polynoms: List<PolynomialFunction>

    init {
        // Determine breaking points
        val elementsCount = ((toEpoch - fromEpoch) / 100).coerceAtMost(10).toInt()
        val step = (toEpoch - fromEpoch) / elementsCount
        val elements: MutableList<Long> = (0..elementsCount - 2).map {
            fromEpoch + step * it
        }.toMutableList()
        elements.add(toEpoch)
        this.elements = elements
        this.polynoms = elements.map {
            PolynomialFunction(doubleArrayOf(Random.nextDouble(-100.0, 100.0), Random.nextDouble(-100.0, 100.0)))
        }
    }

    fun value(v: Long): Double {
        val lastIndex = elements.indexOfLast { v > it }
        if (lastIndex == -1) return polynoms[0].value(v.toDouble() - fromEpoch)
        return polynoms[lastIndex].value(v.toDouble() - fromEpoch)
    }
}

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
        val isMultiple = namePattern.endsWith("*")
        val fromEpoch = from.epochSecond
        val toEpoch = to.epochSecond
        val step = (toEpoch - fromEpoch) / POINT_COUNT
        val timeseriesNames = if (isMultiple) {
            val count = Random.nextInt(1, 10)
            val nameTemplate = namePattern.removeSuffix("*")
            (1..count).map {
                "$nameTemplate$it"
            }
        } else {
            listOf(namePattern)
        }
        return timeseriesNames.map { name ->
            val polynom = MagicPolynom(fromEpoch, toEpoch)
            val timeRange = LongProgression.fromClosedRange(fromEpoch, toEpoch, step).map {
                (it + Random.nextLong(-step / 2, step / 2)).coerceAtMost(toEpoch)
            }
            return@map TimeseriesFrame(
                name,
                emptyMap(),
                timeRange.map { Instant.ofEpochSecond(it) },
                timeRange.map { polynom.value(it) },
            )
        }
    }

    override fun getOrCreate(ownerUUID: String, name: String, tags: Map<String, String>, retention: Int): UUID {
        throw NotImplementedError("You shound't use dummy manager for this")
    }

    override fun update(ownerUUID: String, timeseriesId: UUID, retention: Int) {
        throw NotImplementedError("You shound't use dummy manager for this")
    }

    override fun delete(ownerUUID: String, timeseriesId: UUID) {
        throw NotImplementedError("You shound't use dummy manager for this")
    }

    override fun list(ownerUUID: String): List<TimeseriesInformation> {
        throw NotImplementedError("You shound't use dummy manager for this")
    }

    override fun put(ownerUUID: String, timeseriesId: UUID, measurementValue: Double) {
        throw NotImplementedError("You shound't use dummy manager for this")
    }

    override fun putMany(ownerUUID: String, values: Map<UUID, Double>) {
        throw NotImplementedError("You shound't use dummy manager for this")
    }
}
