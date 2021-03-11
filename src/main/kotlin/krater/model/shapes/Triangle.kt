package krater.model.shapes

import krater.geometry.*
import krater.model.Intersection
import krater.model.Material
import krater.model.Ray
import kotlin.math.abs

open class Triangle(val p1: Tuple, val p2: Tuple, val p3: Tuple,
                    material: Material = Material(),
                    transform: Matrix = IDENTITY_4X4_MATRIX
) : Shape(material, transform) {
    val e1: Tuple = p2 - p1
    val e2: Tuple = p3 - p1
    val normal: Tuple = e2.cross(e1).normalize()

    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple = normal

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
        return listOf(Intersection(t, this, u = u, v = v))
    }

    override val boundingBox: BoundingBox = BoundingBox() + p1 + p2 + p3
}