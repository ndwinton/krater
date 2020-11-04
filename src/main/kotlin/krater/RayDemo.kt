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
    val floor = Sphere(
        transform = scaling(10, 0.01, 10),
        material = Material(
            color = Color(1.0, 0.9, 0.9),
            specular = 0.0
        )
    )

    val leftWall = Sphere(
        transform = scaling(10, 0.01, 10)
            .rotateX(PI / 2)
            .rotateY(-PI / 4)
            .translate(0, 0, 5),
        material = floor.material
    )
    val rightWall = Sphere(
        transform = scaling(10, 0.01, 10)
            .rotateX(PI / 2)
            .rotateY(PI / 4)
            .translate(0, 0, 5),
        material = floor.material
    )

    val middle = Sphere(
        transform = translation(-0.5, 1, 0.5),
        material = Material(
            color = Color(0.1, 1.0, 0.5),
            diffuse = 0.7,
            specular = 0.3
        )
    )

    val right = Sphere(
        transform = scaling(0.5, 0.5, 0.5)
            .translate(1.5, 0.5, 0.5),
        material = Material(
            color = Color(0.5, 1.0, 0.1),
            diffuse = 0.7,
            specular = 0.3
        )
    )

    val left = Sphere(
        transform = scaling(0.33, 0.33, 0.33)
            .translate(-1.5, 0.33, -0.75),
        material = Material(
            color = Color(1.0, 0.8, 0.1),
            diffuse = 0.7,
            specular = 0.3
        )
    )

    val world = World(
        lights = listOf(PointLight(point(-10, 10, -10), Color(1.0, 1.0, 1.0))),
        objects = listOf(floor, leftWall, rightWall, left, middle, right)
    )

    val camera = Camera(1000, 500, PI / 3, viewTransform(point(0, 1.5, -5), point(0, 1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("ray.ppm").writeText(canvas.toPPM())
}