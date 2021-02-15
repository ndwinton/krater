package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.EPSILON
import krater.geometry.Tuple
import krater.geometry.near
import krater.geometry.point
import krater.model.shapes.Shape
import kotlin.math.pow

val DARKNESS = object : Light {
    override val position = point(0, 0, 0)
    override val color = BLACK
    override fun intensityAt(point: Tuple, shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean) = 0.0

    override fun toString() = "Light.DARKNESS"
}

interface Light {
    val position: Tuple
    val color: Color

    fun intensityAt(point: Tuple, shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean): Double

    fun lighting(
        shape: Shape,
        point: Tuple,
        eyev: Tuple,
        normalv: Tuple,
        shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean
    ): Color {
        val objectPoint = shape.worldToObject(point)
        val overPoint = point + normalv * EPSILON
        val intensity = intensityAt(overPoint, shadowEvaluator)
        val lightv = (position - point).normalize()
        val reflectv = -(lightv.reflect(normalv))
        val material = shape.material
        val effectiveColor = shape.material.pattern.colorAtObject(objectPoint) * color
        val effectiveAmbient = effectiveColor * material.ambient

        if (isLightBehindSurface(lightv, normalv) || intensity.near(0.0)) {
            return effectiveAmbient
        }

        val effectiveDiffuse = effectiveColor * material.diffuse * lightv.dot(normalv) * intensity
        val effectiveSpecular = calculateEffectiveSpecular(material, reflectv, eyev) * intensity
        return effectiveAmbient + effectiveDiffuse + effectiveSpecular
    }

    private fun calculateEffectiveSpecular(material: Material, reflectv: Tuple, eyev: Tuple): Color {
        val dotProduct = reflectv.dot(eyev)
        return if (dotProduct <= 0.0) BLACK else color * material.specular * dotProduct.pow(material.shininess)
    }

    private fun isLightBehindSurface(lightv: Tuple, normalv: Tuple) = lightv.dot(normalv) < 0.0
}