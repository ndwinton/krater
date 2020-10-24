package krater.geometry

open class Shape(
    val material: Material = Material(),
    val transform: Matrix = IDENTITY_4X4_MATRIX
) {
    open fun normalAt(point: Tuple) = vector(0, 0, 0)
}