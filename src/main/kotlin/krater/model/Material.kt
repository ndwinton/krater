package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.*
import kotlin.math.pow

class Material(
    val ambient: Double = 0.1,
    val color: Color = Color(1.0, 1.0, 1.0),
    val diffuse: Double = 0.9,
    val shininess: Double = 200.0,
    val specular: Double = 0.9,
    val pattern: ColorProvider = color,
    val reflective: Double = 0.0
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

    fun lighting(light: Light, position: Tuple, eyev: Tuple, normalv: Tuple, inShadow: Boolean, objectPoint: Tuple = position): Color {
        val lightv = (light.position - position).normalize()
        val reflectv = -(lightv.reflect(normalv))
        val effectiveColor = pattern.colorAtObject(objectPoint) * light.intensity
        val effectiveAmbient = effectiveColor * ambient

        if (isLightBehindSurface(lightv, normalv) || inShadow) {
            return effectiveAmbient
        }

        val effectiveDiffuse = effectiveColor * diffuse * lightv.dot(normalv)
        val effectiveSpecular = calculateEffectiveSpecular(reflectv, eyev, light)
        return effectiveAmbient + effectiveDiffuse + effectiveSpecular
    }

    private fun calculateEffectiveSpecular(reflectv: Tuple, eyev: Tuple, light: Light): Color {
        val dotProduct = reflectv.dot(eyev)
        return if (dotProduct <= 0.0) BLACK else light.intensity * specular * dotProduct.pow(shininess)
    }

    private fun isLightBehindSurface(lightv: Tuple, normalv: Tuple) = lightv.dot(normalv) < 0.0
}