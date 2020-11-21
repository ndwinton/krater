package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import kotlin.math.PI
import kotlin.math.sqrt

class ShapeSpec : FunSpec({
    val position = point(0, 0, 0)
    val shape = TestShape()

    test("A shape's default transformation") {
        val s = TestShape()

        s.transform.shouldBe(IDENTITY_4X4_MATRIX)
    }

    test("Changing a shape's default transformation") {
        val t = translation(2, 3, 4)
        val s = TestShape(transform = t)

        s.transform.shouldBe(t)
    }

    test("A shape has a default material") {
        val s = TestShape()

        s.material.shouldBe(Material())
    }

    test("A shape may be assigned a material") {
        val m = Material(ambient = 1.0)
        val s = TestShape(material = m)

        s.material.shouldBe(m)
    }

    test("Intersecting a scaled shape with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = TestShape(transform = scaling(2, 2, 2))

        val xs = s.intersect(r)

        xs.size.shouldBe(0)
        s.savedRay.shouldBe(Ray(point(0, 0, -2.5), vector(0, 0, 0.5)))
    }

    test("Intersecting a translated shape with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = TestShape(transform = translation(5, 0, 0))

        val xs = s.intersect(r)

        xs.size.shouldBe(0)
        s.savedRay.shouldBe(Ray(point(-5, 0, -5), vector(0, 0, 1)))
    }

    test("The normal is a normalized vector") {
        val s = TestShape()

        val n = s.normalAt(point(2, 2, 2))

        n.shouldBe(n.normalize())
    }

    test("Computing the normal on a translated shape") {
        val s = TestShape(transform = translation(0, 1, 0))

        val n = s.normalAt(point(0, 1.70711, -0.70711))

        n.shouldBe(vector(0, 0.70711, -0.70711))
        s.savedPoint.shouldBe(point(0, 0.70711, -0.70711))
    }

    test("Computing the normal on a transformed shape") {
        val s = TestShape(transform = scaling(1, 0.5, 1) * rotationZ(PI / 5.0))

        val n = s.normalAt(point(0, sqrt(2.0) / 2.0, -sqrt(2.0) / 2.0))

        n.shouldBe(vector(0, 0.97014, -0.24254))
        s.savedPoint.shouldBe(point(0.83125, 1.14412, -0.70711))
    }

    // A reduced set of the tests in MaterialSpec, reflecting the pass-through

    test("Lighting with the eye between light and surface") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))

        shape.lighting(light, position, eyev, normalv, false).shouldBe(Color(1.9, 1.9, 1.9))
    }

    test("Lighting with the surface in shadow") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))

        shape.lighting(light, position, eyev, normalv, inShadow = true).shouldBe(Color(0.1, 0.1, 0.1))
    }

    test("Lighting with a pattern applied and shape transformed") {
        val s = TestShape(
            material = Material(
                pattern = StripePattern(WHITE, BLACK),
                ambient = 1.0,
                diffuse = 0.0,
                specular = 0.0,
            ),
            transform = translation(0.5, 0, 0)
        )

        val eyev = vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), WHITE)

        val c1 = s.lighting(light, point(1.4, 0, 0), eyev, normalv, false)
        val c2 = s.lighting(light, point(1.6, 0, 0), eyev, normalv, false)

        c1.shouldBe(WHITE)
        c2.shouldBe(BLACK)
    }
})

class TestShape(transform: Matrix = IDENTITY_4X4_MATRIX, material: Material = Material()) : Shape(transform = transform, material = material) {
    var savedPoint: Tuple? = null
    var savedRay: Ray? = null

    override fun localNormalAt(objectPoint: Tuple): Tuple {
        savedPoint = objectPoint
        return vector(objectPoint.x, objectPoint.y, objectPoint.z)
    }

    override fun localIntersect(objectRay: Ray): List<Intersection> {
        savedRay = objectRay
        return emptyList()
    }

}