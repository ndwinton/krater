package krater

import krater.canvas.Canvas
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = 1000
    val canvas = Canvas(size, size)
    val transform = IDENTITY_4X4_MATRIX.scale(size * 0.4, size * 0.2, size * 0.2).rotateZ(PI / 4).translate(size / 2, size / 2, 0)
    val sphere = Sphere(transform = transform, material = Material(color = Color(1.0, 0.2, 1.0)))
    val light = PointLight(position = point(0, size / 2, -size), intensity = WHITE)

    (0..(size - 1)).forEach { x ->
        (0..(size - 1)).forEach { y ->
            // Cheating by using a viewpoint a far enough away that all rays
            // can be considered parallel.
            val ray = Ray(point(x, y, -10000 * size), vector(0, 0, 1))
            val hit = sphere.intersect(ray).hit()
            if (hit != NO_INTERSECTION) {
                val point = ray.position(hit.t)
                val normal = hit.shape.normalAt(point)
                val eye = -ray.direction
                canvas[x, y] = hit.shape.material.lighting(light, point, eye, normal)
            }
        }
    }
    File("ray.ppm").writeText(canvas.toPPM())
}