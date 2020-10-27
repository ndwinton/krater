package krater.model

import krater.canvas.BLACK
import krater.canvas.Color


class World(val objects: List<Shape> = emptyList(), val light: Light = DARKNESS) {

    fun intersect(ray: Ray): List<Intersection> = objects.flatMap { it.intersect(ray) }.sortedBy { it.t }
    fun shadeHit(computation: PreparedComputation) =
        computation.intersection.shape.material.lighting(light, computation.point, computation.eyev, computation.normalv)

    fun colorAt(ray: Ray): Color {
        val hit = intersect(ray).hit()
        return if (hit == NO_INTERSECTION) BLACK else shadeHit(PreparedComputation(hit, ray))
    }
}