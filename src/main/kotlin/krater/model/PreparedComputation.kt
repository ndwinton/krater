package krater.model

import krater.geometry.EPSILON
import krater.geometry.Tuple

class PreparedComputation(val intersection: Intersection, ray: Ray) {
    val point: Tuple = ray.position(intersection.t)
    val overPoint: Tuple
    val eyev: Tuple = -ray.direction
    val normalv: Tuple
    val inside: Boolean
    val reflectv: Tuple

    init {
        val trueNormal = intersection.shape.normalAt(point)
        if (trueNormal.dot(eyev) < 0) {
            normalv = -trueNormal
            inside = true
        } else {
            normalv = trueNormal
            inside = false
        }
        overPoint = point + normalv * EPSILON
        reflectv = ray.direction.reflect(normalv)
    }
}