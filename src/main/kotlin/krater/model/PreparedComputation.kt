package krater.model

import krater.geometry.EPSILON
import krater.geometry.Tuple
import kotlin.math.pow
import kotlin.math.sqrt

class PreparedComputation(val intersection: Intersection, ray: Ray, allIntersections: List<Intersection> = emptyList()) {
    val point: Tuple = ray.position(intersection.t)
    val overPoint: Tuple
    val underPoint: Tuple
    val eyev: Tuple = -ray.direction
    val normalv: Tuple
    val inside: Boolean
    val reflectv: Tuple
    val n1: Double
    val n2: Double
    val schlickReflectance: Double get() {
        var cos = eyev.dot(normalv)
        if (n1 > n2) {
            val n = n1 / n2
            val sinTSquared = n * n * (1.0 - cos * cos)
            if (sinTSquared > 1.0) return 1.0

            val cosT = sqrt(1.0 - sinTSquared)
            cos = cosT
        }
        val r0 = ((n1 - n2) / (n1 + n2)).pow(2)
        return r0 + (1 - r0) * (1 - cos).pow(5)
    }

    init {
        val trueNormal = intersection.shape.normalAt(point, intersection)
        if (trueNormal.dot(eyev) < 0) {
            normalv = -trueNormal
            inside = true
        } else {
            normalv = trueNormal
            inside = false
        }
        overPoint = point + normalv * EPSILON
        underPoint = point - normalv * EPSILON
        reflectv = ray.direction.reflect(normalv)

        val nValues = computeNValues(intersection, allIntersections)
        n1 = nValues.first
        n2 = nValues.second
    }

    // I'm not particularly happy about the use of mutable state and
    // the deep nesting, but refactoring can wait until the tests
    // all pass ...
    private fun computeNValues(hit: Intersection, allIntersections: List<Intersection>): Pair<Double,Double> {
        val containers = mutableListOf<Shape>()
        var foundN1 = 1.0
        var foundN2 = 1.0
        allIntersections.forEach { intersection ->
            if (intersection == hit && !containers.isEmpty()) {
                foundN1 = containers.last().material.refractiveIndex
            }

            if (containers.contains(intersection.shape)) {
                containers.remove(intersection.shape)
            } else {
                containers.add(containers.size, intersection.shape)
            }

            if (intersection == hit && !containers.isEmpty()) {
                foundN2 = containers.last().material.refractiveIndex
            }
        }
        return Pair(foundN1, foundN2)
    }
}