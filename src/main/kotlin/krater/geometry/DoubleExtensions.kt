package krater.geometry

import kotlin.math.abs
import kotlin.math.roundToLong

const val EPSILON = 0.00001

fun Double.near(other: Double) = when (this) {
    Double.POSITIVE_INFINITY -> other == this
    Double.NEGATIVE_INFINITY -> other == this
    else -> abs(this - other) < EPSILON
}

fun Double.nearHash(): Int = (this / EPSILON / 5).roundToLong().hashCode()