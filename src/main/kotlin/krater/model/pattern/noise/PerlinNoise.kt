package krater.model.pattern.noise

import krater.canvas.ColorProvider
import krater.geometry.*

class PerlinNoise(
    pattern: ColorProvider,
    private val scale: Double = 0.02,
    private val octaves: Int = 1,
    private val persistence: Double = 0.2,
    transform: Matrix = IDENTITY_4X4_MATRIX
): NoiseProvider(pattern = pattern, transform = transform) {
    override fun perturb(point: Tuple): Tuple {
        val perturbedX = point.x + Perlin.octaveNoise(point.x, point.y, point.z, octaves, persistence) * scale
        val perturbedY = point.y + Perlin.octaveNoise(point.x, point.y, point.z + 1, octaves, persistence) * scale
        val perturbedZ = point.z + Perlin.octaveNoise(point.x, point.y, point.z + 2, octaves, persistence) * scale
        return point(perturbedX, perturbedY, perturbedZ)
    }
}