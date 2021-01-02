package krater

import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 500

    val start = System.currentTimeMillis()

    val plane = Plane(material = Material(color = Color(0.0, 1.0, 0.0)))

    val world = World(
        lights = listOf(
            PointLight(point(-10, 10, -10), WHITE),
        ),
        objects = listOf(plane, benzene(translation(0, 3, 0).rotateY(PI / 4)))
    )

    val camera = Camera(size, size, PI / 3, viewTransform(point(0, 5.5, -3.5), point(0, 3, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("benzene.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}

fun carbon() = Sphere(
    material = Material(color = BLACK),
    transform = scaling(0.33, 0.33, 0.33).translate(0, 0, -1)
)

fun hydrogen() = Sphere(transform = scaling(0.2, 0.2, 0.2).translate(0, 0, -1.75))

fun ccBond() = Cylinder(
    material = Material(transparency = 0.6),
    minimum = 0.0,
    maximum = 1.0,
    transform = scaling(0.125, 1, 0.125)
        .rotateZ(-PI / 2)
        .rotateY(-PI / 6)
        .translate(0, 0, -1)
)

fun chBond() = Cylinder(
    material = Material(transparency = 0.6),
    minimum = 0.0,
    maximum = 0.75,
    transform = scaling(0.125, 1, 0.125)
        .rotateZ(-PI / 2)
        .rotateY(PI / 2)
        .translate(0, 0, -1)
)

fun sideUnit(transform: Matrix) = Group(
    transform = transform,
    shapes = listOf(carbon(), ccBond(), hydrogen(), chBond())
)

fun benzene(transform: Matrix = IDENTITY_4X4_MATRIX): Group = Group(
    transform = transform,
    shapes = (0..5).map { n -> sideUnit(rotationY(n * PI / 3)) }
)
