package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.Tuple
import krater.geometry.point

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
}