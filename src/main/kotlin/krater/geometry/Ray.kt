package krater.geometry

class Ray(val origin: Tuple, val direction: Tuple) {
    fun position(t: Double): Tuple = origin + (direction * t)
    fun transform(m: Matrix) = Ray(m * origin, m * direction)
}