package krater.model

import krater.canvas.Color
import krater.geometry.*


abstract class Shape(
    val material: Material = Material(),
    val transform: Matrix = IDENTITY_4X4_MATRIX,
) {
    val inverseTransform = transform.inverse()
    val transposedInverse = inverseTransform.transpose()
    // Not really happy with mutable+nullable value, but it's the easiest way
    // to stay in line with the book.
    var parent: Shape? = null
        set(value) = if (field != null) throw IllegalAccessException("Can't set parent more than once") else field = value

    fun normalAt(point: Tuple): Tuple {
        val objectPoint = worldToObject(point)
        val objectNormal = localNormalAt(objectPoint)
        return normalToWorld(objectNormal)
    }
    abstract fun localNormalAt(objectPoint: Tuple): Tuple

    fun intersect(ray: Ray): List<Intersection> {
        val objectSpaceRay = ray.transform(inverseTransform)
        return localIntersect(objectSpaceRay)
    }

    abstract fun localIntersect(objectRay: Ray): List<Intersection>

    fun lighting(light: Light, position: Tuple, eyev: Tuple, normalv: Tuple, inShadow: Boolean): Color {
        return material.lighting(light, position, eyev,normalv, inShadow, worldToObject(position))
    }

    fun worldToObject(point: Tuple): Tuple = inverseTransform * (parent?.worldToObject(point) ?: point)

    fun normalToWorld(normal: Tuple): Tuple {
        val transposed = transposedInverse * normal
        val worldNormal = vector(transposed.x, transposed.y, transposed.z).normalize()
        return parent?.normalToWorld(worldNormal) ?: worldNormal
    }
}