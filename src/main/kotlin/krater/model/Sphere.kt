package krater.model

import krater.geometry.*
import kotlin.math.sqrt

class Sphere(
    override val transform: Matrix = IDENTITY_4X4_MATRIX,
    override val material: Material = Material()
) : Shape(transform = transform, material = material) {

    override fun localIntersect(objectSpaceRay: Ray): List<Intersection> {
        val sphereToRay = objectSpaceRay.origin - point(0, 0, 0)
        val a = objectSpaceRay.direction.dot(objectSpaceRay.direction)
        val b = 2 * objectSpaceRay.direction.dot(sphereToRay)
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

     override fun localNormalAt(objectPoint: Tuple): Tuple = objectPoint - point(0, 0, 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sphere

        if (transform != other.transform) return false
        if (material != other.material) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transform.hashCode()
        result = 31 * result + material.hashCode()
        return result
    }
}