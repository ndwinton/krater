package krater.model

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.Tuple


class World(val objects: List<Shape> = emptyList(), val lights: List<Light> = listOf(DARKNESS)) {

    constructor(baseWorld: World, objects: List<Shape> = baseWorld.objects, lights: List<Light> = baseWorld.lights) : this(objects, lights)

    fun intersect(ray: Ray): List<Intersection> = objects.flatMap { it.intersect(ray) }.sortedBy { it.t }
    fun shadeHit(computation: PreparedComputation) =
        lights.fold(BLACK) { color, light ->
            color + computation.intersection.shape.lighting(
                light,
                computation.point,
                computation.eyev,
                computation.normalv,
                isShadowed(light, computation.overPoint)
            )
        }

    fun colorAt(ray: Ray): Color {
        val hit = intersect(ray).hit()
        return if (hit == NO_INTERSECTION) BLACK else shadeHit(PreparedComputation(hit, ray))
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
}