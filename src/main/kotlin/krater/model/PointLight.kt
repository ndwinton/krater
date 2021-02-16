package krater.model

import krater.canvas.Color
import krater.geometry.Tuple

class PointLight(override val position: Tuple, override val color: Color) : Light {
    override val samples: List<Tuple> = listOf(position)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PointLight

        if (position != other.position) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }

    override fun toString(): String {
        return "PointLight(position=$position, intensity=$color)"
    }
}