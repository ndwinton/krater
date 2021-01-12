package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.Tuple
import krater.model.shapes.Shape
import kotlin.math.sqrt

const val MAX_RECURSION = 5

class World(val objects: List<Shape> = emptyList(), val lights: List<Light> = listOf(DARKNESS)) {

    constructor(baseWorld: World, objects: List<Shape> = baseWorld.objects, lights: List<Light> = baseWorld.lights) : this(objects, lights)

    fun intersect(ray: Ray): List<Intersection> = objects.flatMap { it.intersect(ray) }.sortedBy { it.t }

    fun shadeHit(computation: PreparedComputation, remaining: Int = MAX_RECURSION) =
        lights.fold(BLACK) { color, light ->
            color + computation.intersection.shape.lighting(
                light,
                computation.point,
                computation.eyev,
                computation.normalv,
                isShadowed(light, computation.overPoint)
            )
        } + reflectedPlusRefracted(computation, remaining)

    private fun reflectedPlusRefracted(computation: PreparedComputation, remaining: Int = MAX_RECURSION): Color {
        val reflected = reflectedColor(computation, remaining)
        val refracted = refractedColor(computation, remaining)
        val material = computation.intersection.shape.material
        return if (material.reflective > 0.0 && material.transparency > 0.0) {
            val reflectance = computation.schlickReflectance
            reflected * reflectance + refracted * (1.0 - reflectance)
        } else {
            reflected + refracted
        }
    }

    fun colorAt(ray: Ray, remaining: Int = MAX_RECURSION): Color {
        val allIntersections = intersect(ray)
        val hit = allIntersections.hit()
        return if (hit == NO_INTERSECTION) BLACK else shadeHit(PreparedComputation(hit, ray, allIntersections), remaining)
    }

    fun isShadowed(light: Light, point: Tuple): Boolean {
        val vector = light.position - point
        val distance = vector.magnitude()
        val direction = vector.normalize()
        val ray = Ray(point, direction)
        val intersections = intersect(ray).filter { it.shape.material.shadow }
        val hit = intersections.hit()
        return hit != NO_INTERSECTION && hit.t < distance
    }

    fun reflectedColor(comps: PreparedComputation, remaining: Int = MAX_RECURSION): Color {
        if (remaining == 0 || comps.intersection.shape.material.reflective == 0.0) return BLACK
        val reflectRay = Ray(comps.overPoint, comps.reflectv)
        val color = colorAt(reflectRay, remaining - 1)
        return color * comps.intersection.shape.material.reflective
    }

    fun refractedColor(comps: PreparedComputation, remaining: Int = MAX_RECURSION): Color {
        val nRatio = comps.n1 / comps.n2
        val cosI = comps.eyev.dot(comps.normalv)
        val sinTSquared = (nRatio * nRatio) * (1 - cosI * cosI)
        return when {
            remaining == 0 -> BLACK // Recursion too deep
            comps.intersection.shape.material.transparency == 0.0 -> BLACK // Opaque
            sinTSquared > 1.0 -> BLACK // Total internal reflection
            else -> {
                val cosT = sqrt(1.0 - sinTSquared)
                val direction = comps.normalv * (nRatio * cosI - cosT) - comps.eyev * nRatio
                val refractRay = Ray(comps.underPoint, direction)
                colorAt(refractRay, remaining - 1) * comps.intersection.shape.material.transparency
            }
        }
    }
}