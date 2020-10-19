package krater.geometry

data class Intersection(val t: Double, val obj: Shape)

val NO_INTERSECTION = Intersection(Double.NaN, object : Shape() {})

fun List<Intersection>.hit(): Intersection = this.filter { it.t >= 0 }.minByOrNull { it.t } ?: NO_INTERSECTION