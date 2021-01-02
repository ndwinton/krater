package krater.model

import krater.geometry.*
import kotlin.math.abs

class Triangle(val p1: Tuple, val p2: Tuple, val p3: Tuple,
               material: Material = Material(),
               transform: Matrix = IDENTITY_4X4_MATRIX
) : Shape(material, transform) {
    val e1: Tuple
    val e2: Tuple
    val normal: Tuple

    init {
        e1 = p2 - p1
        e2 = p3 - p1
        normal = e2.cross(e1).normalize()
    }

    override fun localNormalAt(objectPoint: Tuple): Tuple = normal

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        val dirCrossE2 = objectRay.direction.cross(e2)
        val determinant = e1.dot(dirCrossE2)
        if (abs(determinant) < EPSILON) return emptyList()

        val f = 1.0 / determinant
        val p1ToOrigin = objectRay.origin - p1
        val u = f * p1ToOrigin.dot(dirCrossE2)
        if (u < 0.0 || u > 1.0) return emptyList()

        val originCrossE1 = p1ToOrigin.cross(e1)
        val v = f * objectRay.direction.dot(originCrossE1)
        if (v < 0.0 || (u + v) > 1.0) return emptyList()

        val t = f * e2.dot(originCrossE1)
        return listOf(Intersection(t, this))
    }
}