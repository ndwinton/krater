package krater

import krater.canvas.Color
import krater.geometry.*
import krater.model.*
import krater.model.pattern.*
import krater.model.pattern.noise.PerlinNoise
import krater.model.shapes.*
import java.io.File
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.random.Random

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 500

    val start = System.currentTimeMillis()

    val floor = Plane(
        material = Material(
            ambient = 0.2,
            color = Color(1.0, 0.9, 0.9),
            specular = 0.0,
            reflective = 0.1,
            transparency = 0.0,
            pattern = PerlinNoise(
                pattern = Stripe(Color(0.75, 0.75, 0.2), Color(1.0, 1.0, 0.4), rotationY(PI/2).scale(0.25, 1, 0.25), 2),
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

    val world = World(
        lights = listOf(
            AreaLight(point(-4, 9, -10), vector(0.5, 0, 0), 4, vector(0, 0.5, 0), 4, Color(1.0, 1.0, 1.0))
        ),
        objects = listOf(floor, /*sky,*/ tree(4))
    )

    val camera = Camera(size, size, PI / 6, viewTransform(point(0, 5, -5), point(0, 2, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("tree.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}

val leafTexture = Material(color = Color(0.0, 0.7, 0.0), transparency = 0.5)
val trunkTexture = Material(color = Color(0.5, 0.3, 0.0))

fun tree(level: Int): Shape {
    if (level == 0 || (level == 1 && Random.nextInt(0, 10) > 6)) return Sphere(
        material = leafTexture,
        transform = scaling(
            Random.nextDouble(0.4, 0.7),
            Random.nextDouble(0.4, 0.7),
            Random.nextDouble(0.4, 0.7)
        )
    )

    val trunk = Cylinder(minimum = 0.0, maximum = 1.0, closed = true, material = trunkTexture, transform = scaling(0.1, 1, 0.1))
    val branchCount = (Random.nextInt().absoluteValue % 3) + 2
    val branches = (1..branchCount).map {
        val shrink = Random.nextDouble(0.6, 0.8)
        Group(
            shapes = listOf(tree(level - 1)),
            transform = scaling(shrink, shrink, shrink)
                .rotateZ(-PI / Random.nextDouble(6.0, 9.0))
                .rotateY(it * (Random.nextDouble(1.5, 2.5) * PI) / branchCount)
                .translate(0, 1, 0)
        )
    }

    return Group(shapes = branches + trunk)
}