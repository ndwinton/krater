package krater.model

import krater.geometry.*

abstract class Shape(
    open val material: Material = Material(),
    open val transform: Matrix = IDENTITY_4X4_MATRIX
) {
    fun normalAt(point: Tuple): Tuple {
        val inverse = transform.inverse()
        val objectPoint = inverse * point
        val objectNormal = localNormalAt(objectPoint)
        val uncorrected = inverse.transpose() * objectNormal
        val worldNormal = vector(uncorrected.x, uncorrected.y, uncorrected.z)
        return worldNormal.normalize()
    }
    abstract fun localNormalAt(objectPoint: Tuple): Tuple

    fun intersect(ray: Ray): List<Intersection> {
        val objectSpaceRay = ray.transform(transform.inverse())
        return localIntersect(objectSpaceRay)
    }

    abstract fun localIntersect(objectRay: Ray): List<Intersection>
}