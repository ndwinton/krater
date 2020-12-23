package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import krater.geometry.point
import krater.geometry.vector
import krater.model.Cube
import krater.model.Ray

class CubeSpec : FunSpec({

    test("A ray intersects a cube") {
        val c = Cube()
        table(
            headers("name","orgin", "direction", "t1", "t2"),
            row("+x",   point(5, 0.5, 0),   vector(-1, 0, 0),   4.0,    6.0),
            row("-x",   point(-5,0.5, 0),   vector(1, 0, 0),    4.0,    6.0),
            row("+y",   point(0.5, 5, 0),   vector(0, -1, 0),   4.0,    6.0),
            row("-y",   point(0.5, -5, 0),  vector(0, 1, 0),    4.0,    6.0),
            row("+z",   point(0.5, 0, 5),   vector(0, 0, -1),   4.0,    6.0),
            row("-z",   point(0.5, 0, -5),  vector(0, 0, 1),    4.0,    6.0),
            row("in",   point(0, 0.5, 0),   vector(0, 0, 1),    -1, 1),
        ).forAll { name, origin, direction, t1, t2 ->
            val r = Ray(origin, direction)
            val xs = c.localIntersect(r)

            xs.size.shouldBe(2)
            xs[0].t.shouldBe(t1)
            xs[1].t.shouldBe(t2)
        }
    }

    test("A ray misses a cube") {
        val c = Cube()

        table(
            headers("origin", "direction"),
            row(point(-2, 0, 0), vector(0.2673, 0.5345, 0.8018)),
            row(point(0, -2, 0), vector(0.8018, 0.2673, 0.5345)),
            row(point(0, 0, -2), vector(0.5345, 0.8018, 0.2673)),
            row(point(2, 0, 2), vector(0, 0, -1)),
            row(point(0, 2, 2), vector(0, -1, 0)),
            row(point(2, 2, 0), vector(-1, 0, 0)),
        ).forAll { origin, direction ->
            val r = Ray(origin, direction)
            val xs = c.localIntersect(r)

            xs.shouldBeEmpty()
        }
    }

    test("The normal on the surface of a cube") {
        val c = Cube()
        table(
            headers("point", "normal"),
            row(point(1, 0.5, -0.8), vector(1, 0, 0)),
            row(point(-1, 0.2, 0.9), vector(-1, 0, 0)),
            row(point(-0.4, 1, -0.1), vector(0, 1, 0)),
            row(point(0.3, -1, -0.7), vector(0, -1, 0)),
            row(point(-0.6, 0.3, 1), vector(0, 0, 1)),
            row(point(0.4, 0.4, -1), vector(0, 0, -1)),
            row(point(1, 1, 1), vector(1, 0, 0)),
            row(point(-1, -1, -1), vector(-1, 0, 0)),
        ).forAll { point, normal ->
            c.localNormalAt(point).shouldBe(normal)
        }
    }
})