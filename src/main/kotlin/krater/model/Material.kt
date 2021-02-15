package krater.model

import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.*

class Material(
    val ambient: Double = 0.1,
    val color: Color = Color(1.0, 1.0, 1.0),
    val diffuse: Double = 0.9,
    val shininess: Double = 200.0,
    val specular: Double = 0.9,
    val pattern: ColorProvider = color,
    val reflective: Double = 0.0,
    val transparency: Double = 0.0,
    val refractiveIndex: Double = 1.0,
    val shadow: Boolean = true,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Material

        if (!ambient.near(other.ambient)) return false
        if (color != other.color) return false
        if (!diffuse.near(other.diffuse)) return false
        if (!shininess.near(other.shininess)) return false
        if (!specular.near(other.specular)) return false
        if (pattern != other.pattern) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ambient.nearHash()
        result = 31 * result + color.hashCode()
        result = 31 * result + diffuse.nearHash()
        result = 31 * result + shininess.nearHash()
        result = 31 * result + specular.nearHash()
        result = 31 * result + pattern.hashCode()
        return result
    }
}