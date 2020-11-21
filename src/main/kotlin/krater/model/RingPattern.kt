package krater.model

import krater.canvas.Color
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.near
import kotlin.math.floor
import kotlin.math.sqrt

class RingPattern(val a: Color, val b: Color, transform: Matrix) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color =
        if (floor(sqrt(point.x * point.x + point.z * point.z) % 2.0).near(0.0)) a
        else b
}