package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.*
import kotlin.math.pow

abstract class Shape(
    val material: Material = Material(),
    val transform: Matrix = IDENTITY_4X4_MATRIX
) {
    val inverseTransform = transform.inverse()

    fun normalAt(point: Tuple): Tuple {
        val objectPoint = inverseTransform * point
        val objectNormal = localNormalAt(objectPoint)
        val uncorrected = inverseTransform.transpose() * objectNormal
        val worldNormal = vector(uncorrected.x, uncorrected.y, uncorrected.z)
        return worldNormal.normalize()
    }
    abstract fun localNormalAt(objectPoint: Tuple): Tuple

    fun intersect(ray: Ray): List<Intersection> {
        val objectSpaceRay = ray.transform(inverseTransform)
        return localIntersect(objectSpaceRay)
    }

    abstract fun localIntersect(objectRay: Ray): List<Intersection>

    fun lighting(light: Light, position: Tuple, eyev: Tuple, normalv: Tuple, inShadow: Boolean): Color {
        return material.lighting(light, inverseTransform * position, eyev,normalv, inShadow)
    }
}