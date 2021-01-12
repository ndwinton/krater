package krater.model

import krater.geometry.Tuple
import krater.geometry.vector
import krater.model.shapes.Shape

data class Intersection(val t: Double, val shape: Shape, val u: Double = Double.NaN, val v: Double = Double.NaN)

val NO_INTERSECTION = Intersection(Double.NaN, object : Shape() {
    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple = vector(0, 0, 0)
    override fun localIntersect(objectRay: Ray): List<Intersection> = emptyList()
})

fun List<Intersection>.hit(): Intersection = this.filter { it.t >= 0 }.minByOrNull { it.t } ?: NO_INTERSECTION