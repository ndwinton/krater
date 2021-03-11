package test.model.shapes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import krater.geometry.point
import krater.geometry.vector
import krater.model.Ray
import krater.model.shapes.Triangle

class TriangleSpec :  FunSpec({

    test("Constructing a triangle") {
        val p1 = point(0, 1, 0)
        val p2 = point(-1, 0, 0)
        val p3 = point(1, 0, 0)

        val t = Triangle(p1, p2, p3)

        t.p1.shouldBe(p1)
        t.p2.shouldBe(p2)
        t.p3.shouldBe(p3)
        t.e1.shouldBe(vector(-1, -1, 0))
        t.e2.shouldBe(vector(1, -1, 0))
        t.normal.shouldBe(vector(0, 0, -1))
    }

    test("Finding the normal on a triangle") {
        val t = Triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))

        val n1 = t.localNormalAt(point(0, 0.5, 0))
        val n2 = t.localNormalAt(point(-0.5, 0.75, 0))
        val n3 = t.localNormalAt(point(0.5, 0.25, 0))

        n1.shouldBe(t.normal)
        n2.shouldBe(t.normal)
        n3.shouldBe(t.normal)
    }

    test("Intersecting a ray parallel to the triangle") {
        val t = Triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
        val r = Ray(point(0, -1, -2), vector(0, 1, 0))

        val xs = t.localIntersect(r)

        xs.shouldBeEmpty()
    }

    test("A ray misses the p1-p3 edge") {
        val t = Triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
        val r = Ray(point(1, 1, -2), vector(0, 0, 1))

        val xs = t.localIntersect(r)

        xs.shouldBeEmpty()
    }

    test("A ray misses the p1-p2 edge") {
        val t = Triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
        val r = Ray(point(-1, 1, -2), vector(0, 0, 1))

        val xs = t.localIntersect(r)

        xs.shouldBeEmpty()
    }

    test("A ray misses the p2-p3 edge") {
        val t = Triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
        val r = Ray(point(0, -1, -2), vector(0, 0, 1))

        val xs = t.localIntersect(r)

        xs.shouldBeEmpty()
    }

    test("A ray strikes a triangle") {
        val t = Triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
        val r = Ray(point(0, 0.5, -2), vector(0, 0, 1))

        val xs = t.localIntersect(r)

        xs.size.shouldBe(1)
        xs[0].t.shouldBe(2.0)
    }

    test("A triangle has a bounding box") {
        val p1 = point(-3, 7, 2)
        val p2 = point(6, 2, -4)
        val p3 = point(2, -1, -1)
        val shape = Triangle(p1, p2, p3)

        shape.boundingBox.min.shouldBe(point(-3, -1, -4))
        shape.boundingBox.max.shouldBe(point(6, 7, 2))
    }
})