package krater.model.pattern

import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.near
import kotlin.math.floor

class Stripe(val a: ColorProvider, val b: ColorProvider, transform: Matrix = IDENTITY_4X4_MATRIX, val repeat: Int = 2) : Pattern(transform) {

    override fun colorAt(point: Tuple): Color =
        if ((floor(point.x) % repeat).near(0.0)) a.colorAtObject(point) else b.colorAtObject(point)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Stripe

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