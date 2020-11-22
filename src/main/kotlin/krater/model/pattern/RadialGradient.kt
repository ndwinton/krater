package krater.model.pattern

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

class RadialGradient(val a: Color, val b: Color, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color {
        val colorDistance = b - a
        val distance = sqrt(point.x * point.x + point.z * point.z)
        val fraction = distance - floor(distance)
        return a + colorDistance * fraction
    }
}