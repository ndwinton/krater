package krater

import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import krater.model.pattern.*
import krater.model.pattern.map.TextureMap
import krater.model.pattern.map.UVChecker
import krater.model.pattern.map.sphericalMap
import krater.model.pattern.noise.PerlinNoise
import krater.model.shapes.*
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
                pattern = Stripe(Color(1.0, 0.75, 0.0), Color(1.0, 1.0, 0.5), rotationY(PI/2).scale(0.25, 1, 0.25)),
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
        transform = scaling(5000, 1, 1).translate(0, 5000, 0).rotateY(PI / 2).rotateX(PI / 4)
    )

    val middle = Sphere(
        transform = translation(-0.5, 0.0, 0.5).scale(0.75, 1.0, 1.0),
        material = Material(
            color = Color(0.1, 1.0, 0.5),
            diffuse = 0.7,
            specular = 0.3,
            ambient = 0.2,
            reflective = 0.05,
            pattern = Stripe(
                Color(1.0, 0.0, 0.0),
                Color(1.0, 0.5, 0.5),
                scaling(0.5, 0.5, 0.5).rotateZ(PI / 4)
            ) + TextureMap(
                texture = UVChecker(16, 8, WHITE, BLACK),
                mappingFunction = ::sphericalMap,
                transform = rotationY(PI / 4)
            )
        )
    )

    val right = Sphere(
        transform = scaling(0.5, 0.5, 0.5)
            .translate(1.5, 1.5, 0.5),
        material = Material(
            color = BLACK,
            diffuse = 0.0,
            ambient = 0.0,
            specular = 1.0,
            shininess = 300.0,
            reflective = 0.9,
            transparency = 1.0,
            refractiveIndex = 1.5,

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

    val midGroup = Group(shapes = listOf(middle, left), transform = rotationZ(-PI / 4))

    val box = Cube(
        transform = rotationY(PI / 4).scale(0.5, 0.5, 0.5).translate(1.5, 0.5, 0.5),
        material = Material(
            reflective = 0.3,
            pattern = PerlinNoise(
                Gradient(Color(0.9, 0.9, 0.7), Color(0.5, 0.5, 0.5),
                    transform = scaling(0.25, 1, 1).rotateX(PI / 5).rotateY(PI / 3).rotateZ(PI  / 4)),
                scale = 0.5,
                octaves = 5,
                persistence = 0.9
            )
        )
    )

    val pipe = Cylinder(
        minimum = -1.0,
        maximum = 1.0,
        material = Material(reflective = 0.9, ambient = 0.0, diffuse = 0.0),
        transform = scaling(0.5, 1, 0.5).rotateX(PI / 2).rotateY(PI / 4).translate(-3, 0.5, 4)
    )

    val cylinder = Cylinder(
        minimum = -1.0,
        maximum = 1.0,
        closed = true,
        material = Material(reflective = 0.9, ambient = 0.0, diffuse = 0.0),
        transform = scaling(0.5, 1, 0.5).rotateX(PI / 2).rotateY(-PI / 4).translate(-3, 1.5, 4)
    )

    val cone = Cone(
        minimum = -1.0,
        maximum = -.0,
        closed = true,
        transform = translation(1, 1, 5).scale(1, 3, 1),
        material = Material(transparency = 0.9, refractiveIndex = 1.5, reflective = 0.9, ambient = 0.0, diffuse = 0.0)
    )

    val world = World(
        lights = listOf(
            //PointLight(point(-10, 10, -10), Color(0.75, 0.5, 0.25)),
            AreaLight(point(-10, 10, -10),
                vector(5, 0, 0), 5,
                vector(0, 5, 0), 5,
                Color(0.75, 0.5, 0.25)
            ),
            AreaLight(point(-5, 10, -10),
                vector(1, 0, 0), 4,
                vector(0, 1, 0), 4,
                Color(0.25, 0.5, 0.75))
        ),
        objects = listOf(floor, midGroup, right, sky, box, pipe, cylinder, cone)
    )

    val camera = Camera(size * 2, size, PI / 3, viewTransform(point(0, 1.5, -5), point(0, 1, 0), vector(0, 1, 0)))

    val canvas = camera.render(world)

    File("ray.ppm").writeText(canvas.toPPM())

    val stop = System.currentTimeMillis()
    println("Completed in %.4g seconds".format((stop - start) / 1000.0))
}