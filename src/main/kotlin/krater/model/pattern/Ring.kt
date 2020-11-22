package krater.model.pattern

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.near
import krater.model.pattern.Pattern
import kotlin.math.floor
import kotlin.math.sqrt

class Ring(val a: Color, val b: Color, transform: Matrix = IDENTITY_4X4_MATRIX) : Pattern(transform = transform) {
    override fun colorAt(point: Tuple): Color =
        if (floor(sqrt(point.x * point.x + point.z * point.z) % 2.0).near(0.0)) a
        else b
}