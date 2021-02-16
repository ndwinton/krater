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
    override val samples = listOf(position)

    override fun toString() = "Light.DARKNESS"
}

interface Light {
    val position: Tuple
    val color: Color
    val samples: List<Tuple>

    fun lighting(
        shape: Shape,
        point: Tuple,
        eyev: Tuple,
        normalv: Tuple,
        shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean
    ): Color {
        val objectPoint = shape.worldToObject(point)
        val overPoint = point + normalv * EPSILON
        val material = shape.material
        val effectiveColor = material.pattern.colorAtObject(objectPoint) * color
        val effectiveAmbient = effectiveColor * material.ambient
        val currentSamples = samples
        val summedColor = currentSamples.map { samplePoint ->
            val lightv = (samplePoint - point).normalize()
            val reflectv = -(lightv.reflect(normalv))
            val intensity = intensityFromSampleAtPoint(samplePoint, overPoint, shadowEvaluator)

            if (isLightBehindSurface(lightv, normalv) || intensity.near(0.0)) {
                effectiveAmbient
            } else {
                val effectiveDiffuse = effectiveColor * material.diffuse * lightv.dot(normalv) * intensity
                val effectiveSpecular = calculateEffectiveSpecular(material, reflectv, eyev) * intensity
                effectiveAmbient + effectiveDiffuse + effectiveSpecular
            }
        }.reduce { acc, color ->  acc + color }
        return Color(
            summedColor.red / currentSamples.size,
            summedColor.green / currentSamples.size,
            summedColor.blue / currentSamples.size
        )
    }

    fun intensityAt(point: Tuple, shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean) =
        samples.map { samplePoint -> intensityFromSampleAtPoint(samplePoint, point, shadowEvaluator) }.average()

    private fun intensityFromSampleAtPoint(
        samplePoint: Tuple,
        point: Tuple,
        shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean
    ) = if (shadowEvaluator(samplePoint, point)) 0.0 else 1.0

    private fun calculateEffectiveSpecular(material: Material, reflectv: Tuple, eyev: Tuple): Color {
        val dotProduct = reflectv.dot(eyev)
        return if (dotProduct <= 0.0) BLACK else color * material.specular * dotProduct.pow(material.shininess)
    }

    private fun isLightBehindSurface(lightv: Tuple, normalv: Tuple) = lightv.dot(normalv) < 0.0
}