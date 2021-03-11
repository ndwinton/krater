package krater.model.shapes

import krater.geometry.EPSILON
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.point
import krater.model.Ray
import java.lang.Double.max
import java.lang.Double.min
import kotlin.math.absoluteValue

class BoundingBox(
    val min: Tuple = point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
    val max: Tuple = point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
) {
    operator fun plus(point: Tuple): BoundingBox = BoundingBox(
        min = point(min(min.x, point.x), min(min.y, point.y), min(min.z, point.z)),
        max = point(max(max.x, point.x), max(max.y, point.y), max(max.z, point.z))
    )

    operator fun plus(other: BoundingBox): BoundingBox = this + other.min + other.max

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoundingBox

        if (min != other.min) return false
        if (max != other.max) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

    fun contains(point: Tuple): Boolean =
        (min.x .. max.x).contains(point.x) &&
                (min.y .. max.y).contains(point.y) &&
                (min.z .. max.z).contains(point.z)

    fun contains(other: BoundingBox): Boolean = contains(other.min) && contains(other.max)

    fun transform(matrix: Matrix): BoundingBox =
        listOf(
            min,
            point(min.x, min.y, max.z),
            point(min.x, max.y, min.z),
            point(min.x, max.y, max.z),
            point(max.x, min.y, min.z),
            point(max.x, min.y, max.z),
            point(max.x, max.y, min.z),
            max
        ).fold(BoundingBox()) { current, next -> current + (matrix * next) }

    fun isIntersectedBy(ray: Ray): Boolean {
        val (xTMin, xTMax) = checkAxis(ray.origin.x, ray.direction.x, min.x, max.x)
        val (yTMin, yTMax) = checkAxis(ray.origin.y, ray.direction.y, min.y, max.y)
        val (zTMin, zTMax) = checkAxis(ray.origin.z, ray.direction.z, min.z, max.z)

        val tMin = maxOf(xTMin, yTMin, zTMin)
        val tMax = minOf(xTMax, yTMax, zTMax)

        return tMin <= tMax
    }

    private fun checkAxis(origin: Double, direction: Double, lower: Double, higher: Double): Pair<Double,Double> {
        val tMinNumerator = (lower - origin)
        val tMaxNumerator = (higher - origin)

        val tMinMax = if (direction.absoluteValue >= EPSILON) Pair(tMinNumerator / direction, tMaxNumerator / direction)
        else Pair(tMinNumerator * Double.POSITIVE_INFINITY, tMaxNumerator * Double.POSITIVE_INFINITY)

        return if (tMinMax.first > tMinMax.second) Pair(tMinMax.second, tMinMax.first) else tMinMax
    }

    override fun toString(): String {
        return "BoundingBox(min=$min, max=$max)"
    }
}