package krater.model.pattern.map

import krater.geometry.Tuple

object PlanarMap : Mapping {
    override fun map(point: Tuple): UVPoint = UVPoint(point.x % 1.0, point.z % 1.0)
}