package krater.model

import krater.geometry.*
import kotlin.math.absoluteValue

class Cube(transform: Matrix = IDENTITY_4X4_MATRIX,
           material: Material = Material()) : Shape(transform = transform, material = material) {
    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple =
        when (maxOf(objectPoint.x.absoluteValue, objectPoint.y.absoluteValue, objectPoint.z.absoluteValue)) {
            objectPoint.x.absoluteValue -> vector(objectPoint.x, 0, 0)
            objectPoint.y.absoluteValue -> vector(0, objectPoint.y, 0)
            else -> vector(0, 0, objectPoint.z)
        }

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        val (xTMin, xTMax) = checkAxis(objectRay.origin.x, objectRay.direction.x)
        val (yTMin, yTMax) = checkAxis(objectRay.origin.y, objectRay.direction.y)
        val (zTMin, zTMax) = checkAxis(objectRay.origin.z, objectRay.direction.z)

        val tMin = maxOf(xTMin, yTMin, zTMin)
        val tMax = minOf(xTMax, yTMax, zTMax)

        return if (tMin > tMax) emptyList() else listOf(Intersection(tMin, this), Intersection(tMax, this))
    }

    private fun checkAxis(origin: Double, direction: Double): Pair<Double,Double> {
        val tMinNumerator = (-1.0 - origin)
        val tMaxNumerator = (1.0 - origin)

        val tMinMax = if (direction.absoluteValue >= EPSILON) Pair(tMinNumerator / direction, tMaxNumerator / direction)
        else Pair(tMinNumerator * Double.POSITIVE_INFINITY, tMaxNumerator * Double.POSITIVE_INFINITY)

        return if (tMinMax.first > tMinMax.second) Pair(tMinMax.second, tMinMax.first) else tMinMax
    }
}