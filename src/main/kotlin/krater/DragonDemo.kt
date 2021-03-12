package krater

import krater.canvas.Color
import krater.geometry.*
import krater.model.*
import krater.model.pattern.*
import krater.model.pattern.noise.PerlinNoise
import krater.model.shapes.Plane
import krater.model.wavefront.ObjParser
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 500

    val start = System.currentTimeMillis()

    val floor = Plane(
        material = Material(
            color = Color(1.0, 0.9, 0.9),
            specular = 0.0,
            reflective = 0.1,
            transparency = 0.0,
            pattern = PerlinNoise(
                pattern = Stripe(Color(0.95, 0.95, 1.0), Color(0.8, 0.8, 0.9), rotationY(PI/2).scale(0.25, 1, 0.25)),
                scale = 2.0,
                octaves = 10,
                persistence = 0.5
        ),
    ))

    val dragon = ObjParser.fromFile(
        File("src/main/resources/dragon.obj"),
        Material(color = Color(0.1, 0.75, 0.2), ambient = 0.1)
    ).toGroup(translation(1, 0.12, 0).scale(0.5, 0.5, 0.5).rotateY(-PI / 6)).divide(50)

    val world = World(
        lights = listOf(
            AreaLight(point(-5, 10, -10),
                vector(1, 0, 0), 4,
                vector(0, 1, 0), 4,
                Color(1.0, 1.0, 1.0))
        ),
        objects = listOf(floor, dragon)
    )

    val camera = Camera(size, size, PI / 3, viewTransform(point(0, 1.5, -4), point(0, 1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("dragon.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}