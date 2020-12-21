package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import krater.geometry.*
import krater.model.*
import kotlin.math.sqrt

class IntersectionSpec : FunSpec({

    test("An intersection encapsulates t and object") {
        val s = Sphere()

        val i = Intersection(3.5, s)

        i.t.shouldBe(3.5)
        i.shape.shouldBe(s)
    }

    test("The hit when all intersections have positive t") {
        val s = Sphere()
        val i1 = Intersection(1.0, s)
        val i2 = Intersection(2.0, s)
        val xs = listOf(i1, i2)

        val i = xs.hit()

        i.shouldBe(i1)
    }

    test("The hit when some intersections have negative t") {
        val s = Sphere()
        val i1 = Intersection(-1.0, s)
        val i2 = Intersection(1.0, s)
        val xs = listOf(i1, i2)

        val i = xs.hit()

        i.shouldBe(i2)
    }

    test("The hit when all intersections have negative t") {
        val s = Sphere()
        val i1 = Intersection(-2.0, s)
        val i2 = Intersection(-1.0, s)
        val xs = listOf(i1, i2)

        val i = xs.hit()

        i.shouldBe(NO_INTERSECTION)
    }

    test("The hit is always the lowest non-negative intersection") {
        val s = Sphere()
        val i1 = Intersection(5.0, s)
        val i2 = Intersection(7.0, s)
        val i3 = Intersection(-3.0, s)
        val i4 = Intersection(2.0, s)
        val xs = listOf(i1, i2, i3, i4)

        val i = xs.hit()

        i.shouldBe(i4)
    }

    test("Pre-computing the state of an intersection") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val shape = Sphere()
        val i = Intersection(4.0, shape)

        val comps = PreparedComputation(i, r)

        comps.intersection.shouldBe(i)
        comps.point.shouldBe(point(0, 0, -1))
        comps.eyev.shouldBe(vector(0, 0, -1))
        comps.normalv.shouldBe(vector(0, 0, -1))
    }

    test("The hit, when an intersection occurs on the outside") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val shape = Sphere()
        val i = Intersection(4.0, shape)

        val comps = PreparedComputation(i, r)

        comps.inside.shouldBeFalse()
    }

    test("The hit, when an intersection occurs on the inside") {
        val r = Ray(point(0, 0, 0), vector(0, 0, 1))
        val shape = Sphere()
        val i = Intersection(1.0, shape)

        val comps = PreparedComputation(i, r)

        comps.point.shouldBe(point(0, 0, 1))
        comps.eyev.shouldBe(vector(0, 0, -1))
        comps.inside.shouldBeTrue()
        comps.normalv.shouldBe(vector(0, 0, -1))
    }

    test("Recomputing the reflection vector") {
        val shape = Plane()
        val r = Ray(point(0, 1, -1), vector(0, -sqrt(2.0) / 2.0, sqrt(2.0) / 2.0))
        val i = Intersection(sqrt(2.0), shape)

        val comps = PreparedComputation(i, r)

        comps.reflectv.shouldBe(vector(0, sqrt(2.0) / 2.0, sqrt(2.0) / 2.0))
    }

    test("Finding n1 and n2 at various intersections") {
        table(
            headers("index", "n1", "n2"),
            row(0, 1.0, 1.5),
            row(1, 1.5, 2.0),
            row(2, 2.0, 2.5),
            row(3, 2.5, 2.5),
            row(4, 2.5, 1.5),
            row(5, 1.5, 1.0),
        ).forAll { index, n1, n2 ->
            val a = Sphere(
                material = Material(transparency = 1.0, refractiveIndex = 1.5),
                transform = scaling(2, 2, 2)
            )
            val b = Sphere(
                material = Material(transparency = 1.0, refractiveIndex = 2.0),
                transform = translation(0, 0, -0.25)
            )
            val c = Sphere(
                material = Material(transparency = 1.0, refractiveIndex = 2.5),
                transform = translation(0, 0, 0.25)
            )
            val r = Ray(point(0, 0, -4), vector(0, 0, 1))
            val xs = listOf(
                Intersection(2.0, a),
                Intersection(2.75, b),
                Intersection(3.25, c),
                Intersection(4.75, b),
                Intersection(5.25, c),
                Intersection(6.0, a),
            )

            val comps = PreparedComputation(xs[index], r, xs)

            comps.n1.shouldBe(n1)
            comps.n2.shouldBe(n2)
        }
    }

    test("The under point is offset below the surface") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val shape = Sphere(material = Material(transparency = 1.0, refractiveIndex = 1.5), transform = translation(0, 0, 1))
        val i = Intersection(5.0, shape)
        val xs = listOf(i)
        val comps = PreparedComputation(i, r, xs)

        comps.underPoint.z.shouldBeGreaterThan(EPSILON / 2)
        comps.point.z.shouldBeLessThan(comps.underPoint.z)
    }
})