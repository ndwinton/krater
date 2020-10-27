package krater.model

import krater.geometry.Tuple
import krater.geometry.vector

data class Intersection(val t: Double, val shape: Shape)

val NO_INTERSECTION = Intersection(Double.NaN, object : Shape() {
    override fun normalAt(point: Tuple): Tuple = vector(0, 0, 0)
    override fun intersect(ray: Ray): List<Intersection> = emptyList()
})

fun List<Intersection>.hit(): Intersection = this.filter { it.t >= 0 }.minByOrNull { it.t } ?: NO_INTERSECTION