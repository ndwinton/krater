package krater.canvas

import krater.geometry.near
import krater.geometry.nearHash
import java.lang.Integer.max
import kotlin.math.min
import kotlin.math.roundToInt

class Color(val red: Double, val green: Double, val blue: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color

        if (!red.near(other.red)) return false
        if (!green.near(other.green)) return false
        if (!blue.near(other.blue)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = red.nearHash()
        result = 31 * result + green.nearHash()
        result = 31 * result + blue.nearHash()
        return result
    }

    operator fun plus(other: Color) = Color(red + other.red, green + other.green, blue + other.blue)
    operator fun minus(other: Color) = Color(red - other.red, green - other.green, blue - other.blue)
    operator fun times(scalar: Double) = Color(red * scalar, green * scalar, blue * scalar)
    operator fun times(other: Color) = Color(red * other.red, green * other.green, blue * other.blue)

    private fun scale(value: Double, limit: Int) = max(0, min((limit * value).roundToInt(), limit))
    fun toScaledTriple(limit: Int) = Triple(scale(red, limit), scale(green, limit), scale(blue, limit))
}