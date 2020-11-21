package krater

import krater.canvas.Color
import krater.geometry.*
import krater.model.*
import krater.model.pattern.Stripe
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = 250
    val floor = Plane(
        material = Material(
            color = Color(1.0, 0.9, 0.9),
            specular = 0.0
        )
    )

    val middle = Sphere(
        transform = translation(-0.5, 0.5, 0.5),
        material = Material(
            color = Color(0.1, 1.0, 0.5),
            diffuse = 0.7,
            specular = 0.3,
            pattern = Stripe(
                Color(1.0, 0.0, 0.0),
                Color(1.0, 0.5, 0.5),
                scaling(0.5, 0.5, 0.5).rotateZ(PI / 4)
            )
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
        lights = listOf(
            PointLight(point(-10, 10, -10), Color(0.75, 0.5, 0.25)),
            PointLight(point(-5, 10, -10), Color(0.25, 0.5, 0.75))
        ),
        objects = listOf(floor, left, middle, right)
    )

    val camera = Camera(size * 2, size, PI / 3, viewTransform(point(0, 1.5, -5), point(0, 1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("ray.ppm").writeText(canvas.toPPM())
}