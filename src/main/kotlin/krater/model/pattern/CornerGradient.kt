package krater.model.pattern

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import kotlin.math.floor
import kotlin.math.sqrt

class CornerGradient(val a: Color, val b: Color, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color {
        val colorDistance = b - a
        val fractionX = point.x - floor(point.x)
        val fractionZ = point.z - floor(point.z)
        val fraction = sqrt(fractionX * fractionX + fractionZ * fractionZ)
        return a + colorDistance * fraction
    }
}