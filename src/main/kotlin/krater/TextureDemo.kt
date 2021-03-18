package krater

import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import krater.model.pattern.Checker
import krater.model.pattern.map.TextureMap
import krater.model.pattern.map.planarMap
import krater.model.pattern.map.sphericalMap
import krater.model.shapes.Plane
import krater.model.shapes.Sphere
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 400

    val start = System.currentTimeMillis()

    val sphere = Sphere(
        material = Material(
            pattern = TextureMap(
                texture = Checker(uFrequency = 20, vFrequency = 10, a = Color(0.0, 0.5, 0.0), b =WHITE),
                mappingFunction = ::sphericalMap,
                transform = scaling(1.0, 0.5, 2.0).rotateZ(PI / 4).rotateX(-PI / 4)
            ),
            ambient = 0.2,
            specular = 0.4,
            shininess = 10.0,
            diffuse = 0.6
        )
    )

    val plane = Plane(
        material = Material(
            pattern = TextureMap(
                texture = Checker(uFrequency = 4, vFrequency = 4, a = Color(0.5, 0.0, 0.0), b = WHITE),
            mappingFunction = ::planarMap
            )
        )
    )

    val world = World(
        lights = listOf(
            PointLight(point(-10, 10, -10), WHITE),
        ),
        objects = listOf(sphere, plane)
    )

    val camera = Camera(size, size, 0.5, viewTransform(point(1, 2, -5), point(0, 0, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("texture.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}
