package krater.model.pattern

import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.point

class Solid(val color: ColorProvider, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform) {
    override fun colorAt(point: Tuple): Color = color.colorAtObject(point)
    override fun colorAtObject(objectPoint: Tuple): Color = color.colorAtObject(objectPoint)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Solid

        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}