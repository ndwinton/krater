package test.model.shapes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import krater.geometry.*
import krater.model.shapes.Group
import krater.model.Ray
import krater.model.shapes.Cylinder
import krater.model.shapes.Sphere

class GroupSpec : FunSpec({

    test("Creating a group") {
        val g = Group()

        g.transform.shouldBe(IDENTITY_4X4_MATRIX)
    }

    test("Adding a child to a group") {
        val s = TestShape()
        val g = Group(shapes = listOf(s))

        g.shapes.shouldContain(s)
        s.parent.shouldBe(g)
    }

    test("Intersecting a ray with an empty group") {
        val g = Group()
        val r = Ray(point(0, 0, 0), vector(0, 0, 1))

        val xs = g.localIntersect(r)

        xs.shouldBeEmpty()
    }

    test("Intersecting a ray with a non-empty group") {
        val s1 = Sphere()
        val s2 = Sphere(transform = translation(0, 0, -3))
        val s3 = Sphere(transform = translation(5, 0, 0))
        val g = Group(shapes = listOf(s1, s2, s3))
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))

        val xs = g.localIntersect(r)

        xs.size.shouldBe(4)
        xs[0].shape.shouldBe(s2)
        xs[1].shape.shouldBe(s2)
        xs[2].shape.shouldBe(s1)
        xs[3].shape.shouldBe(s1)
    }

    test("Intersecting a transformed group") {
        val s = Sphere(transform = translation(5, 0, 0))
        val g = Group(shapes = listOf(s), transform = scaling(2, 2, 2))
        val r = Ray(point(10, 0, -10), vector(0, 0, 1))

        val xs = g.intersect(r)

        xs.size.shouldBe(2)
    }

    test("A group has a bounding box that contains its children") {
        val s = Sphere(transform = translation(2, 5, -3) * scaling(2, 2, 2))
        val c = Cylinder(minimum = -2.0, maximum = 2.0, transform = translation(-4, -1, 4) * scaling(0.5, 1, 0.5))
        val shape = Group(shapes = listOf(s, c))
        shape.boundingBox.min.shouldBe(point(-4.5, -3, -5))
        shape.boundingBox.max.shouldBe(point(4, 7, 4.5))
    }
})