package krater.model

import krater.geometry.*
import kotlin.math.sqrt

class Cylinder(material: Material = Material(),
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
            else -> vector(objectPoint.x, 0, objectPoint.z)
        }
    }

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        val a = objectRay.direction.x * objectRay.direction.x + objectRay.direction.z * objectRay.direction.z
        // Ray parallel to the y-axis
        if (a.near(0.0)) return intersectCap(objectRay)

        val b = 2 * objectRay.origin.x * objectRay.direction.x + 2 * objectRay.origin.z * objectRay.direction.z
        val c = objectRay.origin.x * objectRay.origin.x + objectRay.origin.z * objectRay.origin.z - 1
        val discriminant = b * b - 4 * a * c
        return if (discriminant < 0.0) emptyList() // No intersection
        else listOf(
            Intersection((-b - sqrt(discriminant)) / (2.0 * a), this),
            Intersection((-b + sqrt(discriminant)) / (2.0 * a), this)
        ).sortedBy { it.t }.filter {
            val hitY = objectRay.origin.y + it.t * objectRay.direction.y
            minimum < hitY && hitY < maximum
        } + intersectCap(objectRay)
    }

    // Is intersection at t within a radius of 1 from y-axis
    private fun checkCap(ray: Ray, t: Double): Boolean {
        val x = ray.origin.x + t * ray.direction.x
        val z = ray.origin.z + t * ray.direction.z
        return (x * x + z * z) <= 1.0
    }

    private fun intersectCap(ray: Ray): List<Intersection> {
        if (!closed || ray.direction.y.near(0.0)) return emptyList()

        val lowerT = (minimum - ray.origin.y) / ray.direction.y
        val upperT = (maximum - ray.origin.y) / ray.direction.y
        val hitsLower = checkCap(ray, lowerT)
        val hitsUpper = checkCap(ray, upperT)
        return when {
            hitsLower && hitsUpper -> listOf(Intersection(lowerT, this), Intersection(upperT, this))
            hitsLower -> listOf(Intersection(lowerT, this))
            hitsUpper -> listOf(Intersection(upperT, this))
            else -> emptyList()
        }
    }
}