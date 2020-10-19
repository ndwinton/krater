package krater.geometry

import kotlin.math.sqrt

class Sphere : Shape() {

    fun intersect(ray: Ray): List<Intersection> {
        val transformedRay = ray.transform(transform.inverse())
        val sphereToRay = transformedRay.origin - point(0, 0, 0)
        val a = transformedRay.direction.dot(transformedRay.direction)
        val b = 2 * transformedRay.direction.dot(sphereToRay)
        val c = sphereToRay.dot(sphereToRay) - 1
        val discriminant = b * b - 4 * a * c

        return if (discriminant < 0.0) {
            emptyList()
        } else {
            listOf(
                Intersection((-b - sqrt(discriminant)) / (2 * a), this),
                Intersection((-b + sqrt(discriminant)) / (2 * a), this)
            )
        }
    }
}