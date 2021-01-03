package krater

import krater.canvas.BLACK
import krater.canvas.Color
import krater.geometry.*
import krater.model.*
import krater.model.pattern.*
import krater.model.pattern.noise.PerlinNoise
import krater.model.wavefront.ObjParser
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 250

    val start = System.currentTimeMillis()

    val floor = Plane(
        material = Material(
            color = Color(1.0, 0.9, 0.9),
            specular = 0.0,
            reflective = 0.1,
            transparency = 0.0,
            pattern = PerlinNoise(
                pattern = Stripe(Color(0.1, 0.75, 0.2), Color(0.1, 1.0, 0.4), rotationY(PI/2).scale(0.25, 1, 0.25)),
                scale = 1.5,
                octaves = 5,
                persistence = 0.5
        ),
    ))

    val sky = Plane(
        material = Material(
            ambient = 0.7,
            pattern = Gradient(Color(0.5, 0.5, 1.0), Color(0.1, 0.8, 0.8), scaling(1, 1, 1000))
        ),
        transform = scaling(10000, 1, 1).translate(0, 5000, 0).rotateY(PI / 2).rotateX(PI / 4)
    )

    val teapot = ObjParser.fromFile(File("src/main/resources/teapot.obj")).toGroup(scaling(0.5, 0.5, 0.5))

    val world = World(
        lights = listOf(
            PointLight(point(-10, 10, -10), Color(0.75, 0.5, 0.25)),
            PointLight(point(-5, 10, -10), Color(0.25, 0.5, 0.75))
        ),
        objects = listOf(floor, sky, teapot)
    )

    val camera = Camera(size, size, PI / 3, viewTransform(point(0, 1.5, -5), point(0, 1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("teapot.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}