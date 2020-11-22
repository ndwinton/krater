package krater.model.pattern

import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import kotlin.math.floor
import kotlin.math.sqrt

class CornerGradient(val a: ColorProvider, val b: ColorProvider, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color {
        val colorDistance = b.colorAtObject(point) - a.colorAtObject(point)
        val fractionX = point.x - floor(point.x)
        val fractionZ = point.z - floor(point.z)
        val fraction = sqrt(fractionX * fractionX + fractionZ * fractionZ)
        return a.colorAtObject(point) + colorDistance * fraction
    }
}