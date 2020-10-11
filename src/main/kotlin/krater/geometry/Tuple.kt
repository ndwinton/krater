package krater.geometry

import java.lang.IllegalArgumentException
import kotlin.math.sqrt

class Tuple(val x: Double, val y: Double, val z: Double, val w: Double) {

    fun isPoint() = w != 0.0
    fun isVector() = w == 0.0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tuple

        if (!x.near(other.x)) return false
        if (!y.near(other.y)) return false
        if (!z.near(other.z)) return false
        if (!w.near(other.w)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.nearHash()
        result = 31 * result + y.nearHash()
        result = 31 * result + z.nearHash()
        result = 31 * result + w.nearHash()
        return result
    }

    override fun toString() = "Tuple(x=$x, y=$y, z=$z, w=$w)"

    operator fun plus(other: Tuple) = Tuple(x + other.x, y + other.y, z + other.z, w + other.w)
    operator fun minus(other: Tuple) = Tuple(x - other.x, y - other.y, z - other.z, w - other.w)
    operator fun unaryMinus() = Tuple(-x, -y, -z, -w)
    operator fun times(num: Number) = Tuple(x * num.toDouble(), y * num.toDouble(), z * num.toDouble(), w * num.toDouble())
    operator fun div(num: Number) = Tuple(x / num.toDouble(), y / num.toDouble(), z / num.toDouble(), w / num.toDouble())
    fun magnitude() = sqrt(x * x + y * y + z * z + w * w)
    fun normalize(): Tuple {
        val magnitude = this.magnitude()
        return Tuple(x / magnitude, y / magnitude, z / magnitude, w / magnitude)
    }

    fun dot(other: Tuple): Double = x * other.x + y * other.y + z * other.z + w * other.w
    fun cross(other: Tuple): Any {
        if (!isVector()) throw IllegalArgumentException("Cross product only valid on vectors")
        return vector(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    }
}

fun point(x: Number, y: Number, z: Number) = Tuple(x.toDouble(), y.toDouble(), z.toDouble(), 1.0)

fun vector(x: Number, y: Number, z: Number) = Tuple(x.toDouble(), y.toDouble(), z.toDouble(), 0.0)

