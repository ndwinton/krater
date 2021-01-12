package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import krater.model.shapes.Sphere
import kotlin.math.PI
import kotlin.math.sqrt

class CameraSpec : FunSpec({

    test("Constructing a camera") {
        val hsize = 160
        val vsize = 120
        val fieldOfView = PI / 2

        val c = Camera(hsize, vsize, fieldOfView)

        c.hsize.shouldBe(160)
        c.vsize.shouldBe(120)
        c.fieldOfView.shouldBe(PI / 2)
        c.transform.shouldBe(IDENTITY_4X4_MATRIX)
    }

    test("The pixel size for a horizontal canvas") {
        val c = Camera(200, 125, PI / 2)

        c.pixelSize.near(0.01).shouldBe(true)
    }

    test("The pixel size for a vertical canvas") {
        val c = Camera(125, 200, PI / 2)

        c.pixelSize.near(0.01).shouldBe(true)
    }

    test("Constructing a ray through the centre of the canvas") {
        val c = Camera(201, 101, PI / 2)

        val r = c.rayForPixel(100, 50)

        r.origin.shouldBe(point(0, 0, 0))
        r.direction.shouldBe(vector(0, 0, -1))
    }

    test("Constructing a ray through a corner of the canvas") {
        val c = Camera(201, 101, PI / 2)

        val r = c.rayForPixel(0, 0)

        r.origin.shouldBe(point(0, 0, 0))
        r.direction.shouldBe(vector(0.66519, 0.33259, -0.66851))
    }

    test("Constructing a ray when the canvas is transformed") {
        val c = Camera(201, 101, PI / 2, rotationY(PI / 4) * translation(0, -2, 5))

        val r = c.rayForPixel(100, 50)

        r.origin.shouldBe(point(0, 2, -5))
        r.direction.shouldBe(vector(sqrt(2.0) / 2, 0, -sqrt(2.0) / 2))
    }

    test("Rendering a world with a camera") {
        val light = PointLight(point(-10, 10, -10), WHITE)
        val defaultWorld = World(
            lights = listOf(light),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
                Sphere(transform = scaling(0.5, 0.5, 0.5))
            )
        )
        val from = point(0, 0, -5)
        val to = point(0, 0, 0)
        val up = vector(0, 1, 0)
        val c = Camera(11, 11, PI / 2, viewTransform(from, to, up))

        val image = c.render(defaultWorld)

        image[5, 5].shouldBe(Color(0.38066, 0.47583, 0.2855))
    }
})