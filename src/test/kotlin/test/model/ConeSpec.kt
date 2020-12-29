package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.geometry.near
import krater.geometry.point
import krater.geometry.vector
import krater.model.Cone
import krater.model.Ray
import kotlin.math.sqrt

class ConeSpec : FunSpec({

    test("Intersecting a cone with a ray") {
        val shape = Cone()
        table(
            headers("origin", "direction", "t0", "t1"),
            row(point(0, 0, -5), vector(0, 0, 1), 5.0, 5.0),
            row(point(0, 0, -5), vector(1, 1, 1), 8.66025, 8.66025),
            row(point(1, 1, -5), vector(-0.5, -1, 1), 4.55006, 49.44994),
        ).forAll { origin, direction, t0, t1 ->
            val r = Ray(origin, direction.normalize())

            val xs = shape.localIntersect(r)

            xs.size.shouldBe(2)
            xs[0].t.near(t0).shouldBe(true)
            xs[1].t.near(t1).shouldBe(true)
        }
    }

    test("Intersecting a cone with a ray parallel to one of its halves") {
        val shape = Cone()
        val direction = vector(0, 1, 1)
        val r = Ray(point(0, 0, -1), direction.normalize())

        val xs = shape.localIntersect(r)

        xs.size.shouldBe(1)
        xs[0].t.near(0.35355).shouldBe(true)
    }

    test("Intersecting a cone's end caps") {
        val shape = Cone(minimum = -0.5, maximum = 0.5, closed = true)
        table(
            headers("point", "direction", "count"),
            row(point(0, 0, -5), vector(0, 1, 0), 0),
            row(point(0, 0, -0.25), vector(0, 1, 1), 2),
            row(point(0, 0, -0.25), vector(0, 1, 0), 4),
        ).forAll { point, direction, count ->
            val r = Ray(point, direction.normalize())
            val xs = shape.localIntersect(r)

            xs.size.shouldBe(count)
        }
    }

    test("Normal vector on a cone") {
        val shape = Cone()
        table(
            headers("point", "normal"),
            row(point(0, 0, 0), vector(0, 0, 0)),
            row(point(1, 1, 1), vector(1, -sqrt(2.0), 1)),
            row(point(-1, -1, 0), vector(-1, 1, 0))
        ).forAll { point, normal ->
            shape.localNormalAt(point).shouldBe(normal)
        }
    }
})