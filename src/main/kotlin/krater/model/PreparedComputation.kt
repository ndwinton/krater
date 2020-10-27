package krater.model

import krater.geometry.Tuple

class PreparedComputation(val intersection: Intersection, ray: Ray) {
    val point: Tuple = ray.position(intersection.t)
    val eyev: Tuple = -ray.direction
    val normalv: Tuple
    val inside: Boolean

    init {
        val trueNormal = intersection.shape.normalAt(point)
        if (trueNormal.dot(eyev) < 0) {
            normalv = -trueNormal
            inside = true
        } else {
            normalv = trueNormal
            inside = false
        }
    }
}