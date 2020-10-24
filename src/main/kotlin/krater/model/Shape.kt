package krater.model

import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.vector

open class Shape(
    val material: Material = Material(),
    val transform: Matrix = IDENTITY_4X4_MATRIX
) {
    open fun normalAt(point: Tuple) = vector(0, 0, 0)
}