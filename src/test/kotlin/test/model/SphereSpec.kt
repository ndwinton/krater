package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.geometry.*
import krater.model.Intersection
import krater.model.Material
import krater.model.Ray
import krater.model.Sphere
import kotlin.math.PI
import kotlin.math.sqrt

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

        xs[0].shape.shouldBe(s)
        xs[1].shape.shouldBe(s)
    }

    test("A sphere's default transformation") {
        val s = Sphere()

        s.transform.shouldBe(IDENTITY_4X4_MATRIX)
    }

    test("Changing a sphere's default transformation") {
        val t = translation(2, 3, 4)
        val s = Sphere(transform = t)

        s.transform.shouldBe(t)
    }

    test("Intersecting a scaled sphere with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = Sphere(transform = scaling(2, 2, 2))

        val xs = s.intersect(r)

        xs.shouldBe(listOf(Intersection(3.0, s), Intersection(7.0, s)))
    }

    test("Intersecting a translated sphere with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val s = Sphere(transform = translation(5, 0, 0))

        val xs = s.intersect(r)

        xs.size.shouldBe(0)
    }

    test("The normal on a sphere at a point on the x axis") {
        val s = Sphere()

        val n = s.normalAt(point(1, 0, 0))

        n.shouldBe(vector(1, 0, 0))
    }

    test("The normal on a sphere at a point on the y axis") {
        val s = Sphere()

        val n = s.normalAt(point(0, 1, 0))

        n.shouldBe(vector(0, 1, 0))
    }

    test("The normal on a sphere at a point on the z axis") {
        val s = Sphere()

        val n = s.normalAt(point(0, 0, 1))

        n.shouldBe(vector(0, 0, 1))
    }

    test("The normal on a sphere at a non-axial point") {
        val s = Sphere()
        val root3by3 = sqrt(3.0) / 3.0

        val n = s.normalAt(point(root3by3, root3by3, root3by3))

        n.shouldBe(vector(root3by3, root3by3, root3by3))
    }

    test("The normal is a normalized vector") {
        val s = Sphere()
        val root3by3 = sqrt(3.0) / 3.0

        val n = s.normalAt(point(root3by3, root3by3, root3by3))

        n.shouldBe(n.normalize())
    }

    test("Computing the normal on a translated sphere") {
        val s = Sphere(transform = translation(0, 1, 0))

        val n = s.normalAt(point(0, 1.70711, -0.70711))

        n.shouldBe(vector(0, 0.70711, -0.70711))
    }

    test("Computing the normal on a transformed sphere") {
        val s = Sphere(transform = scaling(1, 0.5, 1) * rotationZ(PI / 5.0))

        val n = s.normalAt(point(0, sqrt(2.0) / 2.0, -sqrt(2.0) / 2.0))

        n.shouldBe(vector(0, 0.97014, -0.24254))
    }

    test("A sphere has a default material") {
        val s = Sphere()

        s.material.shouldBe(Material())
    }

    test("A sphere may be assigned a material") {
        val m = Material(ambient = 1.0)
        val s = Sphere(material = m)

        s.material.shouldBe(m)
    }
})