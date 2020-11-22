package krater.model.pattern

import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple

abstract class Pattern(val transform: Matrix = IDENTITY_4X4_MATRIX) : ColorProvider {
    private val inverseTransform = transform.inverse()

    abstract fun colorAt(point: Tuple): Color

    // Modified from book definition - objectPoint assumed transformed into object space
    override fun colorAtObject(objectPoint: Tuple): Color {
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