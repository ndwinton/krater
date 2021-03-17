package krater.model.pattern.map

import krater.geometry.Tuple
import krater.geometry.vector
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2

data class UVPoint(val u: Double, val v: Double)

fun sphericalMap(point: Tuple): UVPoint {
    // compute the azimuthal angle
    // -π < theta <= π
    // angle increases clockwise as viewed from above,
    // which is opposite of what we want, but we'll fix it later.
    val theta = atan2(point.x, point.z)

    // vec is the vector pointing from the sphere's origin (the world origin)
    // to the point, which will also happen to be exactly equal to the sphere's
    // radius.
    val vec = vector(point.x, point.y, point.z)
    val radius = vec.magnitude()

    // # compute the polar angle
    // # 0 <= phi <= π
    val phi = acos(point.y / radius)

    // -0.5 < rawU <= 0.5
    val rawU = theta / (2 * PI)

    // 0 <= u < 1
    // here's also where we fix the direction of u. Subtract it from 1,
    // so that it increases counterclockwise as viewed from above.
    val u = 1 - (rawU + 0.5)

    // we want v to be 0 at the south pole of the sphere,
    // and 1 at the north pole, so we have to "flip it over"
    // by subtracting it from 1.
    val v = 1 - phi / PI

    return UVPoint(u, v)
}