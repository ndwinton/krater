package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.Tuple
import krater.geometry.point

val DARKNESS = object : Light {
    override val position = point(0, 0, 0)
    override val intensity = BLACK
    override fun toString() = "Light.DARKNESS"
}

interface Light {
    val position: Tuple
    val intensity: Color
}