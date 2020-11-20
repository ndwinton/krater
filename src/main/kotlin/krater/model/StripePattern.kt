package krater.model

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.near
import kotlin.math.floor

class StripePattern(val a: Color, val b: Color, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform) {

    override fun colorAt(point: Tuple): Color = if ((floor(point.x) % 2).near(0.0)) a else b

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StripePattern

        if (a != other.a) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + a.hashCode()
        result = 31 * result + b.hashCode()
        return result
    }
}