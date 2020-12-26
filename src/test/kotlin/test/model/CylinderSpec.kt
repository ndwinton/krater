package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import krater.geometry.near
import krater.geometry.point
import krater.geometry.vector
import krater.model.Cylinder
import krater.model.Ray

class CylinderSpec : FunSpec({

    test("A ray misses a cylinder") {
        val cyl = Cylinder()
        table(
            headers("origin", "direction"),
            row(point(1, 0, 0), vector(0, 1, 0)),
            row(point(0, 0, 0), vector(0, 1, 0)),
            row(point(0, 0, -5), vector(1, 1, 1)),
        ).forAll { origin, direction ->
            val r = Ray(origin, direction.normalize())

            val xs = cyl.localIntersect(r)

            xs.shouldBeEmpty()
        }
    }

    test("A ray strikes a cylinder") {
        val cyl = Cylinder()
        table(
            headers("origin", "direction", "t0", "t1"),
            row(point(1, 0, -5), vector(0, 0, 1), 5.0, 5.0),
            row(point(0, 0, -5), vector(0, 0, 1), 4.0, 6.0),
            row(point(0.5, 0, -5), vector(0.1, 1, 1), 6.80798, 7.08872),
        ).forAll { origin, direction, t0, t1 ->
            val r = Ray(origin, direction.normalize())

            val xs = cyl.localIntersect(r)

            xs.size.shouldBe(2)
            xs[0].t.near(t0).shouldBe(true)
            xs[1].t.near(t1).shouldBe(true)
        }
    }

    test("Normal vector on a cylinder") {
        val cyl = Cylinder()
        table(
            headers("point", "normal"),
            row(point(1, 0, 0), vector(1, 0, 0)),
            row(point(0, 5, -1), vector(0, 0, -1)),
            row(point(0, -2, 1), vector(0, 0, 1)),
            row(point(-1, 1, 0), vector(-1, 0, 0))
        ).forAll { point, normal ->
            cyl.localNormalAt(point).shouldBe(normal)
        }
    }

    test("The default minimum and maximum") {
        val cyl = Cylinder()
        cyl.minimum.shouldBe(Double.NEGATIVE_INFINITY)
        cyl.maximum.shouldBe(Double.POSITIVE_INFINITY)
    }

    test("Intersecting a constrained cylinder") {
        val cyl = Cylinder(minimum = 1.0, maximum = 2.0)
        table(
            headers("point", "direction", "count"),
            row(point(0, 1.5, 0), vector(0.1, 1, 0), 0),
            row(point(0, 3, -5), vector(0, 0, 1), 0),
            row(point(0, 0, -5), vector(0, 0, 1), 0),
            row(point(0, 2, -5), vector(0, 0, 1), 0),
            row(point(0, 1, -5), vector(0, 0, 1), 0),
            row(point(0, 1.5, -2), vector(0, 0, 1), 2),
        ).forAll { point, direction, count ->
            val r = Ray(point, direction.normalize())
            val xs = cyl.localIntersect(r)

            xs.size.shouldBe(count)
        }
    }
})