package krater.model

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple

abstract class Pattern(val transform: Matrix = IDENTITY_4X4_MATRIX) {
    private val inverseTransform = transform.inverse()

    abstract fun colorAt(point: Tuple): Color

    open fun colorAtObject(obj: Shape, point: Tuple): Color {
        val objectPoint = obj.inverseTransform * point
        val patternPoint = inverseTransform * objectPoint
        return colorAt(patternPoint)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pattern

        if (transform != other.transform) return false

        return true
    }

    override fun hashCode(): Int {
        return transform.hashCode()
    }
}