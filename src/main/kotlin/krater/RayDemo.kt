package krater

import krater.canvas.Canvas
import krater.canvas.Color
import krater.geometry.*
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val canvas = Canvas(100, 100)
    val sphere = Sphere()
    val red = Color(1.0, 0.0, 0.0)

    sphere.transform = IDENTITY_4X4_MATRIX.scale(40, 20, 1).rotateZ(PI / 4).translate(50, 50, 0)
    (0..99).forEach { x ->
        (0..99).forEach { y ->
            val ray = Ray(point(x, y, -10), vector(0, 0, 1))
            val xs = sphere.intersect(ray)
            if (xs.isNotEmpty()) {
                canvas[x, y] = red
            }
        }
    }
    File("ray.ppm").writeText(canvas.toPPM())
}