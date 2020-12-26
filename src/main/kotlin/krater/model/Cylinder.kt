package krater.model

import krater.geometry.*
import kotlin.math.sqrt

class Cylinder(material: Material = Material(),
               transform: Matrix = IDENTITY_4X4_MATRIX,
               val minimum: Double = Double.NEGATIVE_INFINITY,
               val maximum: Double = Double.POSITIVE_INFINITY) : Shape(material, transform) {
    override fun localNormalAt(objectPoint: Tuple): Tuple = vector(objectPoint.x, 0, objectPoint.z)

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        val a = objectRay.direction.x * objectRay.direction.x + objectRay.direction.z * objectRay.direction.z
        // Ray parallel to the y-axis
        if (a.near(0.0)) return emptyList()

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
        }
    }
}