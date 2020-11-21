package krater.model

import krater.canvas.Color
import krater.geometry.Matrix
import krater.geometry.Tuple
import kotlin.math.floor

class GradientPattern(val a: Color, val b: Color, transform: Matrix) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color {
        val colorDistance = b - a
        val fraction = point.x - floor(point.x)
        return a + colorDistance * fraction
    }
}