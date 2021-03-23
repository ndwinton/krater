package krater

import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import krater.model.pattern.*
import krater.model.pattern.map.*
import krater.model.shapes.Cylinder
import krater.model.shapes.Plane
import krater.model.shapes.Sphere
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 1600

    val start = System.currentTimeMillis()

    val earth = Sphere(
        material = Material(
            pattern = MappedTexture(
                texture = ImageTexture(File("src/main/resources/earthmap1k.jpg")),
                mapping = SphericalMapping,
            ),
            ambient = 0.1,
            specular = 0.1,
            shininess = 10.0,
            diffuse = 0.9
        ),
        transform = rotationY(1.1 * PI).rotateZ(-0.4).translate(0, 1.1, 0)
    )

    val plane = Plane(
        material = Material(
            color = WHITE,
            ambient = 0.0,
            specular = 0.0,
            diffuse = 0.1,
            reflective = 0.4
        )
    )

    val cylinder = Cylinder(
        minimum = 0.0,
        maximum = 0.1,
        closed = true,
        material = Material(
            color = WHITE,
            ambient = 0.0,
            diffuse = 0.2,
            reflective = 0.1
        )
    )

    val world = World(
        lights = listOf(
            PointLight(point(-100, 100, -100), WHITE),
        ),
        objects = listOf(earth, plane, cylinder, earth)
    )

    val camera = Camera(size, size / 2, 0.8, viewTransform(point(1, 2, -10), point(0, 1.1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("earth.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}
