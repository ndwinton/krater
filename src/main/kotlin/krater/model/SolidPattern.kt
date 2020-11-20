package krater.model

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple

class SolidPattern(val color: Color, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform) {
    override fun colorAt(point: Tuple): Color = color
    override fun colorAtObject(obj: Shape, point: Tuple): Color = color

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SolidPattern

        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}