package krater

import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import krater.model.pattern.*
import krater.model.pattern.noise.PerlinNoise
import java.io.File
import kotlin.math.PI

fun main(args: Array<String>) {
    val size = 250
    val floor = Plane(
        material = Material(
            color = Color(1.0, 0.9, 0.9),
            specular = 0.0,
            reflective = 0.1,
            pattern = PerlinNoise(
                pattern = Stripe(Color(1.0, 0.0, 0.0), Color(1.0, 0.5, 0.5), rotationY(PI/2).scale(0.25, 1, 0.25)),
                scale = 1.5,
                octaves = 5,
                persistence = 0.5
        ),
    ))

    val sky = Plane(
        material = Material(
            ambient = 1.0,
            pattern = Gradient(Color(0.5, 0.5, 1.0), Color(0.1, 0.8, 0.8), scaling(1, 1, 1000))
        ),
        transform = scaling(50, 1, 1).rotateX(PI / 2).rotateZ(-PI / 2).translate(0, 0, 100)
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
            ) + Checker(WHITE, BLACK, transform = scaling(0.5, 0.5, 0.5))
        )
    )

    val right = Sphere(
        transform = scaling(0.5, 0.5, 0.5)
            .translate(1.5, 0.5, 0.5),
        material = Material(
            color = WHITE,
            diffuse = 0.0,
            specular = 0.0,
            reflective = 1.0,

//            pattern = Checker(
//                Color(0.5, 1.0, 0.1),
//                Color(0.0, 0.5, 0.0), scaling(0.25, 0.25, 0.25).rotateY(PI / 4)
//            )
        )
    )

    val left = Sphere(
        transform = scaling(0.33, 0.33, 0.33)
            .translate(-1.5, 0.33, -0.75),
        material = Material(
            color = Color(1.0, 0.8, 0.1),
            diffuse = 0.7,
            specular = 0.3,
            pattern = PerlinNoise(Gradient(Color(1.0, 0.8, 0.1), Color(0.0, 0.0, 1.0)), scale = 0.8, octaves = 5, persistence = 0.9)
        )
    )

    val world = World(
        lights = listOf(
            PointLight(point(-10, 10, -10), Color(0.75, 0.5, 0.25)),
            PointLight(point(-5, 10, -10), Color(0.25, 0.5, 0.75))
        ),
        objects = listOf(floor, left, middle, right, /*sky*/)
    )

    val camera = Camera(size * 2, size, PI / 3, viewTransform(point(0, 1.5, -5), point(0, 1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("ray.ppm").writeText(canvas.toPPM())
}