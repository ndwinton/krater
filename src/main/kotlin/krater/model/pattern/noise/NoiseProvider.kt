package krater.model.pattern.noise

import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.model.pattern.Pattern

abstract class NoiseProvider(val pattern: ColorProvider, transform: Matrix = IDENTITY_4X4_MATRIX): Pattern(transform) {
    override fun colorAt(point: Tuple): Color = pattern.colorAtObject(perturb(point))

    abstract fun perturb(point: Tuple): Tuple
}