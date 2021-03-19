package krater.model.pattern.map

import krater.geometry.Tuple

data class UVPoint(val u: Double, val v: Double)

interface Mapping {
    fun map(point: Tuple): UVPoint
}