package krater.model

import krater.canvas.Color
import krater.geometry.Tuple
import kotlin.random.Random

class AreaLight(val corner: Tuple,
                u: Tuple,
                val uSteps: Int,
                v: Tuple,
                val vSteps: Int,
                override val color: Color,
                val jitter: () -> Double = { Random.nextDouble() }
) : Light {
    val uVec = u / uSteps
    val vVec = v /vSteps
    override val position = corner + ((u + v) / 2)

    override fun intensityAt(point: Tuple, shadowEvaluator: (lightPosition: Tuple, point: Tuple) -> Boolean) =
        (0 until uSteps).flatMap { uStep ->
            (0 until vSteps).map { vStep ->
                cellPoint(uStep, vStep)
            }
        }.map {
            if (shadowEvaluator(it, point)) 0.0 else 1.0
        }.average()

    fun cellPoint(uStep: Int, vStep: Int): Tuple = corner + uVec * (uStep + jitter()) + vVec * (vStep + jitter())
}