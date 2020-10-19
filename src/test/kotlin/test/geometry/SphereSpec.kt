package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.geometry.*

class SphereSpec : FunSpec({
    test("A ray intersects a sphere at two points") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = Sphere()

        val xs = s.intersect(r)

        xs.shouldBe(listOf(Intersection(4.0, s), Intersection(6.0, s)))
    }

    test("A ray intersects a sphere at a tangent") {
        val r = Ray(point(0, 1, -5), vector(0, 0, 1))
        val s = Sphere()

        val xs = s.intersect(r)

        xs.shouldBe(listOf(Intersection(5.0, s), Intersection(5.0, s)))
    }

    test("A ray misses a sphere") {
        val r = Ray(point(0, 2, -5), vector(0, 0, 1))
        val s = Sphere()

        val xs = s.intersect(r)

        xs.size.shouldBe(0)
    }

    test("A ray originates inside a sphere") {
        val r = Ray(point(0, 0, 0), vector(0, 0, 1))
        val s = Sphere()

        val xs = s.intersect(r)

        xs.shouldBe(listOf(Intersection(-1.0, s), Intersection(1.0, s)))
    }

    test("A sphere is behind a ray") {
        val r = Ray(point(0, 0, 5), vector(0, 0, 1))
        val s = Sphere()

        val xs = s.intersect(r)

        xs.shouldBe(listOf(Intersection(-6.0, s), Intersection(-4.0, s)))
    }

    test("Intersect sets the object on the intersection") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = Sphere()

        val xs = s.intersect(r)

        xs[0].obj.shouldBe(s)
        xs[1].obj.shouldBe(s)
    }

    test("A sphere's default transformation") {
        val s = Sphere()

        s.transform.shouldBe(IDENTITY_4X4_MATRIX)
    }

    test("Changing a sphere's default transformation") {
        val s = Sphere()
        val t = translation(2, 3, 4)

        s.transform = t
        s.transform.shouldBe(t)
    }

    test("Intersecting a scaled sphere with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = Sphere()

        s.transform = scaling(2, 2, 2)
        val xs = s.intersect(r)

        xs.shouldBe(listOf(Intersection(3.0, s), Intersection(7.0, s)))
    }

    test("Intersecting a translated sphere with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = Sphere()

        s.transform = translation(5, 0, 0)
        val xs = s.intersect(r)

        xs.size.shouldBe(0)
    }
})