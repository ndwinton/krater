package test.model.shapes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import krater.geometry.point
import krater.geometry.vector
import krater.model.shapes.Plane
import krater.model.Ray

class PlaneSpec : FunSpec({

    test("The normal of a plane is constant everywhere") {
        val p = Plane()

        val n1 = p.localNormalAt(point(0, 0, 0))
        val n2 = p.localNormalAt(point(20, 0, -10))
        val n3 = p.localNormalAt(point(-5, 0, 150))

        n1.shouldBe(vector(0, 1, 0))
        n2.shouldBe(vector(0, 1, 0))
        n3.shouldBe(vector(0, 1, 0))
    }

    test("Intersect with a ray parallel to the plane") {
        val p = Plane()
        val r = Ray(point(0, 10, 0), vector(0, 0, 1))

        val xs = p.localIntersect(r)

        xs.shouldBeEmpty()
    }

    test("A ray intersecting a plane from above") {
        val p = Plane()
        val r = Ray(point(0, 4, 0), vector(3, -4, 0).normalize())

        val xs = p.localIntersect(r)

        xs.size.shouldBe(1)
        xs[0].t.shouldBe(5.0)
        xs[0].shape.shouldBe(p)
    }

    test("A ray intersecting a plane from below") {
        val p = Plane()
        val r = Ray(point(0, -2, 0), vector(0, 1, 1))

        val xs = p.localIntersect(r)

        xs.size.shouldBe(1)
        xs[0].t.shouldBe(2.0)
        xs[0].shape.shouldBe(p)
    }

    test("A plane has a bounding box") {
        val plane = Plane()
        plane.boundingBox.min.shouldBe(point(Double.NEGATIVE_INFINITY, 0, Double.NEGATIVE_INFINITY))
        plane.boundingBox.max.shouldBe(point(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY))
    }
})