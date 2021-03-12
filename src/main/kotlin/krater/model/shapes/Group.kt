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
        if (boundingBox.isIntersectedBy(objectRay)) {
            shapes.flatMap { it.intersect(objectRay) }.sortedBy { it.t }
        } else {
            emptyList()
        }

    override fun includes(shape: Shape): Boolean = this.equals(shape) || shapes.any { it.includes(shape) }

    override val boundingBox: BoundingBox =
        shapes.fold(BoundingBox()) { current, next -> current + next.parentSpaceBounds }

    override val parentSpaceBounds: BoundingBox
        get() = if (shapes.isEmpty()) BoundingBox() else super.parentSpaceBounds

    override fun toString(): String {
        return "Group(shapes=$shapes, boundingBox=$boundingBox)"
    }

    fun partition(): Group {
        val (leftBox, rightBox) = boundingBox.split()
        val (inLeft, notLeft) = shapes.partition { leftBox.contains(it.parentSpaceBounds) }
        val (inRight, remainder) = notLeft.partition { rightBox.contains(it.parentSpaceBounds) }
        return Group(shapes = listOf(inLeft, inRight, remainder)
            .filter { it.isNotEmpty() }
            .map { Group(shapes = it) }, transform = transform)
    }

    override fun divide(threshold: Int): Shape {
        val partitioned = if (threshold < shapes.size) partition() else this
        return Group(shapes = partitioned.shapes.map { it.divide(threshold) }, transform = partitioned.transform)
    }
}