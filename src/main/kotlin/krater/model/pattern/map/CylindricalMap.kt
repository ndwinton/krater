package krater.model.pattern.map

import krater.geometry.Tuple
import kotlin.math.PI
import kotlin.math.atan2

object CylindricalMap : Mapping {
    override fun map(point: Tuple): UVPoint {
        val theta = atan2(point.x, point.z)
        val rawU = theta / (2 * PI)
        val u = 1 - (rawU + 0.5)
        val v = point.y % 1.0
        return UVPoint(u, v)    }
}