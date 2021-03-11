package krater.model.shapes

import krater.geometry.*
import krater.model.Intersection
import krater.model.Material
import krater.model.Ray
import kotlin.math.sqrt

class Sphere(
    transform: Matrix = IDENTITY_4X4_MATRIX,
    material: Material = Material()
) : Shape(transform = transform, material = material) {

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        val sphereToRay = objectRay.origin - point(0, 0, 0)
        val a = objectRay.direction.dot(objectRay.direction)
        val b = 2 * objectRay.direction.dot(sphereToRay)
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

    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple = objectPoint - point(0, 0, 0)

    override val boundingBox: BoundingBox = BoundingBox(min = point(-1, -1, -1), max = point(1, 1, 1))

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