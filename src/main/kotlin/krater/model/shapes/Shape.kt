package krater.model.shapes

import krater.canvas.Color
import krater.geometry.*
import krater.model.*


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

    // We can pre-calculate the transformations that may be required at
    // a group level to save multiple repeated identical matrix operations.
    // But we have to use lazy initialisation because the parent will not
    // initially be set.
    val groupedInverseTransform: Matrix by lazy(LazyThreadSafetyMode.PUBLICATION) {
        calculateGroupInverseTransform()
    }
    private fun calculateGroupInverseTransform(): Matrix = inverseTransform * (parent?.calculateGroupInverseTransform() ?: IDENTITY_4X4_MATRIX)

    val groupedTransposedInverse: Matrix by lazy(LazyThreadSafetyMode.PUBLICATION) {
        calculateGroupTransposedInverse()
    }
    private fun calculateGroupTransposedInverse(): Matrix = (parent?.calculateGroupTransposedInverse() ?: IDENTITY_4X4_MATRIX) * transposedInverse

    fun normalAt(point: Tuple, intersection: Intersection = NO_INTERSECTION): Tuple {
        val objectPoint = worldToObject(point)
        val objectNormal = localNormalAt(objectPoint, intersection)
        return normalToWorld(objectNormal)
    }

    abstract fun localNormalAt(objectPoint: Tuple, intersection: Intersection = NO_INTERSECTION): Tuple

    fun intersect(ray: Ray): List<Intersection> {
        val objectSpaceRay = ray.transform(inverseTransform)
        return localIntersect(objectSpaceRay)
    }

    abstract fun localIntersect(objectRay: Ray): List<Intersection>

    fun worldToObject(point: Tuple): Tuple = groupedInverseTransform * point

    fun normalToWorld(normal: Tuple): Tuple {
        val transposed = groupedTransposedInverse * normal
        return vector(transposed.x, transposed.y, transposed.z).normalize()
    }

    open fun includes(shape: Shape) = this.equals(shape)

    open val boundingBox: BoundingBox = BoundingBox()

    val parentSpaceBounds: BoundingBox
        get() = BoundingBox(min = transform * boundingBox.min, max = transform * boundingBox.max)
}