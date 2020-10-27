package krater.model

import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.vector

abstract class Shape(
    open val material: Material = Material(),
    open val transform: Matrix = IDENTITY_4X4_MATRIX
) {
    abstract fun normalAt(point: Tuple): Tuple
    abstract fun intersect(ray: Ray): List<Intersection>
}