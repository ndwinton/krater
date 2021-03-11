package krater.model.shapes

import krater.geometry.*
import krater.model.Intersection
import krater.model.Material
import krater.model.Ray
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sqrt

class Cone(material: Material = Material(),
           transform: Matrix = IDENTITY_4X4_MATRIX,
           val minimum: Double = Double.NEGATIVE_INFINITY,
           val maximum: Double = Double.POSITIVE_INFINITY,
           val closed: Boolean = false,
) : Shape(material, transform) {
    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple {
        val dist = objectPoint.x * objectPoint.x + objectPoint.z * objectPoint.z
        return when {
            dist < 1.0 && objectPoint.y >= maximum - EPSILON -> vector(0, 1, 0)
            dist < 1.0 && objectPoint.y <= minimum + EPSILON -> vector(0, -1, 0)
            else -> {
                val y = sqrt(objectPoint.x * objectPoint.x + objectPoint.z * objectPoint.z)
                vector(objectPoint.x, if (objectPoint.y > 0) - y else y, objectPoint.z)
            }
        }
    }

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        val dir = objectRay.direction
        val org = objectRay.origin
        val a = dir.x * dir.x - dir.y * dir.y + dir.z * dir.z
        val b = 2 * org.x * dir.x - 2 * org.y * dir.y + 2 * org.z * dir.z
        val c = org.x * org.x - org.y * org.y + org.z * org.z
        val discriminant = b * b - 4 * a * c

        return when {
            a.near(0.0) && b.near(0.0) -> emptyList()
            a.near(0.0) -> listOf(Intersection(-c / (2 * b), this)) + intersectCap(objectRay)
            discriminant < 0.0 -> emptyList()
            else -> listOf(
                Intersection((-b - sqrt(discriminant)) / (2.0 * a), this),
                Intersection((-b + sqrt(discriminant)) / (2.0 * a), this)
            ).sortedBy { it.t }.filter {
                val hitY = objectRay.origin.y + it.t * objectRay.direction.y
                minimum < hitY && hitY < maximum
            } + intersectCap(objectRay)
        }
    }

    // Is intersection at t within radius
    private fun checkCap(ray: Ray, t: Double, radius: Double): Boolean {
        val x = ray.origin.x + t * ray.direction.x
        val z = ray.origin.z + t * ray.direction.z
        return (x * x + z * z) <= radius * radius
    }

    private fun intersectCap(ray: Ray): List<Intersection> {
        if (!closed || ray.direction.y.near(0.0)) return emptyList()

        val lowerT = (minimum - ray.origin.y) / ray.direction.y
        val upperT = (maximum - ray.origin.y) / ray.direction.y
        val hitsLower = checkCap(ray, lowerT, minimum.absoluteValue)
        val hitsUpper = checkCap(ray, upperT, maximum.absoluteValue)
        return when {
            hitsLower && hitsUpper -> listOf(Intersection(lowerT, this), Intersection(upperT, this))
            hitsLower -> listOf(Intersection(lowerT, this))
            hitsUpper -> listOf(Intersection(upperT, this))
            else -> emptyList()
        }
    }

    private val limit = max(abs(minimum), abs(maximum))
    override val boundingBox: BoundingBox = BoundingBox(
        min = point(-limit, minimum, -limit),
        max = point(limit, maximum, limit)
    )
}