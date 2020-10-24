package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.geometry.Intersection
import krater.geometry.NO_INTERSECTION
import krater.geometry.Sphere
import krater.geometry.hit

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
})