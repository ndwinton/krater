package krater

import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import krater.model.pattern.AlignCheck
import krater.model.pattern.MappedCube
import krater.model.pattern.map.*
import krater.model.shapes.Cube
import java.io.File

fun main(args: Array<String>) {
    val size = if (args.isNotEmpty()) args[0].toInt() else 800

    val start = System.currentTimeMillis()

    val red = Color(1.0, 0.0, 0.0)
    val green = Color(0.0, 1.0, 0.0)
    val blue = Color(0.0, 0.0, 1.0)
    val cyan = Color(0.0, 1.0, 1.0)
    val purple = Color(1.0, 0.0, 1.0)
    val yellow = Color(1.0, 1.0, 0.0)
    val brown = Color(1.0, 0.5, 0.0)
    val white = WHITE

    val left = AlignCheck(yellow, cyan, red, blue, brown)
    val front = AlignCheck(cyan, red, yellow, brown, green)
    val right = AlignCheck(red, yellow, purple, green, white)
    val back = AlignCheck(green, purple, cyan, white, blue)
    val up = AlignCheck(brown, cyan, purple, red, yellow)
    val down = AlignCheck(purple, brown, green, blue, white)
    val mapped = Material(
        pattern = MappedCube(left, front, right, back, up, down),
        ambient = 0.2,
        specular = 0.0,
        diffuse = 0.8
    )

    val cube1 = Cube(
        material = mapped,
        transform = rotationY(0.7854).rotateX(0.7854).translate(-6, 2, 0)
    )
    val cube2 = Cube(
        material = mapped,
        transform = rotationY(2.3562).rotateX(0.7854).translate(-2, 2, 0)
    )
    val cube3 = Cube(
        material = mapped,
        transform = rotationY(3.927).rotateX(0.7854).translate(2, 2, 0)
    )
    val cube4 = Cube(
        material = mapped,
        transform = rotationY(5.4978).rotateX(0.7854).translate(6, 2, 0)
    )
    val cube5 = Cube(
        material = mapped,
        transform = rotationY(0.7854).rotateX(-0.7854).translate(-6, -2, 0)
    )
    val cube6 = Cube(
        material = mapped,
        transform = rotationY(2.3562).rotateX(-0.7854).translate(-2, -2, 0)
    )
    val cube7 = Cube(
        material = mapped,
        transform = rotationY(3.927).rotateX(-0.7854).translate(2, -2, 0)
    )
    val cube8 = Cube(
        material = mapped,
        transform = rotationY(5.4978).rotateX(-0.7854).translate(6, -2, 0)
    )

    val world = World(
        lights = listOf(
            PointLight(point(0, 100, -100), Color(0.25, 0.25, 0.25)),
            PointLight(point(0, -100, -100), Color(0.25, 0.25, 0.25)),
            PointLight(point(-100, 0, -100), Color(0.25, 0.25, 0.25)),
            PointLight(point(100, 0, -100), Color(0.25, 0.25, 0.25)),
        ),
        objects = listOf(cube1, cube2, cube3, cube4, cube5, cube6, cube7, cube8)
    )

    val camera = Camera(size, size / 2, 0.8, viewTransform(point(0, 0, -20), point(0, 0, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("cubemap.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}
