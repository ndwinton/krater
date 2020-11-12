package krater.model

import krater.geometry.Matrix
import krater.geometry.Tuple

class Ray(val origin: Tuple, val direction: Tuple) {
    fun position(t: Double): Tuple = origin + (direction * t)
    fun transform(m: Matrix) = Ray(m * origin, m * direction)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ray

        if (origin != other.origin) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }

}