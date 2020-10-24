package krater.model

data class Intersection(val t: Double, val shape: Shape)

val NO_INTERSECTION = Intersection(Double.NaN, object : Shape() {})

fun List<Intersection>.hit(): Intersection = this.filter { it.t >= 0 }.minByOrNull { it.t } ?: NO_INTERSECTION