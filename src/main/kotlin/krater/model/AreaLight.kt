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
        (0 until uSteps).flatMap { u ->
            (0 until vSteps).map { v ->
                cellPoint(u, v)
            }
        }.map {
            if (shadowEvaluator(it, point)) 0.0 else 1.0
        }.average()

    fun cellPoint(u: Int, v: Int): Tuple {
        val j1 = jitter()
        val j2 = jitter()
        println("$j1, $j2")
        return corner + uVec * (u + jitter()) + vVec * (v + jitter())
    }
}