package krater.model

import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple

typealias CsgOperation = (Boolean, Boolean, Boolean) -> Boolean

val UNION: CsgOperation = { leftHit: Boolean, inLeft: Boolean, inRight: Boolean -> (leftHit && !inRight) || (!leftHit && !inLeft) }
val INTERSECT: CsgOperation = { leftHit: Boolean, inLeft: Boolean, inRight: Boolean -> (leftHit && inRight) || (!leftHit && inLeft) }
val DIFFERENCE: CsgOperation = { leftHit: Boolean, inLeft: Boolean, inRight: Boolean -> (leftHit && !inRight) || (!leftHit && inLeft) }

class Csg(
    val operation: CsgOperation,
    val left: Shape,
    val right: Shape,
    transform: Matrix = IDENTITY_4X4_MATRIX
) : Shape(transform = transform) {

    init {
        left.parent = this
        right.parent = this
    }

    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple {
        throw NotImplementedError("localNormalAt should never be called on a CSG object")
    }

    override fun localIntersect(objectRay: Ray): List<Intersection> =
        filterIntersections((left.intersect(objectRay) + right.intersect(objectRay)).sortedBy { it.t })

    fun filterIntersections(list: List<Intersection>): List<Intersection> {
        var inLeft = false
        var inRight = false

        return list.filter {
            val leftHit = left.includes(it.shape)
            val include = operation(leftHit, inLeft, inRight)
            if (leftHit) inLeft = !inLeft else inRight = !inRight
            include
        }
    }

    override fun includes(shape: Shape): Boolean = this.equals(shape) || left.includes(shape) || right.includes(shape)
}