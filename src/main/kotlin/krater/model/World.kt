package krater.model

import krater.canvas.BLACK
import krater.canvas.Color


class World(val objects: List<Shape> = emptyList(), val lights: List<Light> = listOf(DARKNESS)) {

    constructor(baseWorld: World, objects: List<Shape> = baseWorld.objects, lights: List<Light> = baseWorld.lights) : this(objects, lights)

    fun intersect(ray: Ray): List<Intersection> = objects.flatMap { it.intersect(ray) }.sortedBy { it.t }
    fun shadeHit(computation: PreparedComputation) =
        lights.fold(BLACK) { color, light ->
            color + computation.intersection.shape.material.lighting(light, computation.point, computation.eyev, computation.normalv)
        }

    fun colorAt(ray: Ray): Color {
        val hit = intersect(ray).hit()
        return if (hit == NO_INTERSECTION) BLACK else shadeHit(PreparedComputation(hit, ray))
    }
}