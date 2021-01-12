package krater.model.shapes

import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.model.Intersection
import krater.model.Ray

class Group(transform: Matrix = IDENTITY_4X4_MATRIX, val shapes: List<Shape> = emptyList()) : Shape(transform = transform) {

    init {
        shapes.forEach { it.parent = this }
    }

    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple {
        throw NotImplementedError("Should never be called directly")
    }

    override fun localIntersect(objectRay: Ray): List<Intersection> =
        shapes.flatMap { it.intersect(objectRay) }.sortedBy { it.t }

    override fun includes(shape: Shape): Boolean = this.equals(shape) || shapes.any { it.includes(shape) }
}