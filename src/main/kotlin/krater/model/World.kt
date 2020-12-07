package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.Tuple

const val MAX_RECURSION = 4

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
            ) + reflectedColor(computation, remaining)
        }

    fun colorAt(ray: Ray, remaining: Int = MAX_RECURSION): Color {
        val hit = intersect(ray).hit()
        return if (hit == NO_INTERSECTION) BLACK else shadeHit(PreparedComputation(hit, ray), remaining)
    }

    fun isShadowed(light: Light, point: Tuple): Boolean {
        val vector = light.position - point
        val distance = vector.magnitude()
        val direction = vector.normalize()
        val ray = Ray(point, direction)
        val intersections = intersect(ray)
        val hit = intersections.hit()
        return hit != NO_INTERSECTION && hit.t < distance
    }

    fun reflectedColor(comps: PreparedComputation, remaining: Int = MAX_RECURSION): Color {
        if (remaining == 0 || comps.intersection.shape.material.reflective == 0.0) return BLACK
        val reflectRay = Ray(comps.overPoint, comps.reflectv)
        val color = colorAt(reflectRay, remaining - 1)
        return color * comps.intersection.shape.material.reflective
    }
}