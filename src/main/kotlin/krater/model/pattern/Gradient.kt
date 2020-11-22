package krater.model.pattern

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import kotlin.math.floor

class Gradient(val a: Color, val b: Color, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color {
        val colorDistance = b - a
        val fraction = point.x - floor(point.x)
        return a + colorDistance * fraction
    }
}