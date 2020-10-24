package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import krater.geometry.*
import kotlin.math.sqrt

class TupleSpec : FunSpec({

    test("A tuple with w = 1.0 is a point") {
        val a = Tuple(4.3, -4.2, 3.1, 1.0)

        a.x.shouldBe(4.3)
        a.y.shouldBe(-4.2)
        a.z.shouldBe(3.1)
        a.w.shouldBe(1.0)
        a.isPoint().shouldBe(true)
        a.isVector().shouldBe(false)
    }

    test("A tuple with w = 0.0 is a vector") {
        val a = Tuple(4.3, -4.2, 3.1, 0.0)

        a.x.shouldBe(4.3)
        a.y.shouldBe(-4.2)
        a.z.shouldBe(3.1)
        a.w.shouldBe(0.0)
        a.isPoint().shouldBe(false)
        a.isVector().shouldBe(true)
    }

    test("Tuples should compare equal within tolerance") {
        val t1 = Tuple(1.0, 2.0, -3.0, 4.5)
        val t2 = Tuple(1.000005, 1.999995, -3.000009, 4.499999)
        val t3 = Tuple(1.0001, 2.0, -3.0001, 4.4999)

        (t1 == t2).shouldBe(true)
        (t1 == t3).shouldBe(false)
    }

    test("Tuples should hash equal within tolerance") {
        val t1 = Tuple(1.0, 2.0, -3.0, 4.5)
        val t2 = Tuple(1.000005, 1.999995, -3.000009, 4.499999)
        val t3 = Tuple(1.0001, 2.0, -3.0001, 4.4999)

        t1.hashCode().shouldBe(t2.hashCode())
        t1.hashCode().shouldNotBe(t3.hashCode())
    }

    test("point() creates tuples with w = 1") {
        val p = point(4, -4, 3)
        p.shouldBe(Tuple(4.0, -4.0, 3.0, 1.0))
    }

    test("vector() creates tuples with w = 0") {
        val p = vector(4, -4, 3)
        p.shouldBe(Tuple(4.0, -4.0, 3.0, 0.0))
    }

    test("Adding two tuples") {
        val a1 = Tuple(3.0, -2.0, 5.0, 1.0)
        val a2 = Tuple(-2.0, 3.0, 1.0, 0.0)

        (a1 + a2).shouldBe(Tuple(1.0, 1.0, 6.0, 1.0))
    }

    test("Subtracting two points") {
        val p1 = point(3, 2, 1)
        val p2 = point(5, 6, 7)

        (p1 - p2).shouldBe(vector(-2, -4, -6))
    }

    test("Subtracting a vector from a point") {
        val p = point(3, 2, 1)
        val v = vector(5, 6, 7)

        (p - v).shouldBe(point(-2, -4, -6))
    }

    test("Subtracting two vectors") {
        val v1 = vector(3, 2, 1)
        val v2 = vector(5, 6, 7)

        (v1 - v2).shouldBe(vector(-2, -4, -6))
    }

    test("Negating a tuple") {
        val a = Tuple(1.0, -2.0, 3.0, -4.0)

        (-a).shouldBe(Tuple(-1.0, 2.0, -3.0, 4.0))
    }

    test("Multiplying by a scalar") {
        val a = Tuple(1.0, -2.0, 3.0, -4.0)

        (a * 3.5).shouldBe(Tuple(3.5, -7.0, 10.5, -14.0))
    }

    test("Dividing by a scalar") {
        val a = Tuple(1.0, -2.0, 3.0, -4.0)

        (a / 2).shouldBe(Tuple(0.5, -1.0, 1.5, -2.0))
    }

    test("Computing the magnitude of vectors") {
        forAll(
            row(vector(1, 0, 0), 1.0),
            row(vector(0, 1, 0), 1.0),
            row(vector(0, 0, 1), 1.0),
            row(vector(1, 2, 3), sqrt(14.0)),
            row(vector(-1, -2, -3), sqrt(14.0)),
        ) { v, expected ->
            v.magnitude().shouldBe(expected)
        }
    }

    test("Normalizing vectors") {
        forAll(
            row(vector(4, 0, 0), vector(1, 0, 0)),
            row(vector(1, 2, 3), vector(0.26726, 0.53452, 0.80178))
        ) { v, normalized ->
            v.normalize().shouldBe(normalized)
        }
    }

    test("The magnitude of a normalized vector") {
        val v = vector(1, 2, 3)
        val mag = v.normalize().magnitude()
        mag.near(1.0).shouldBe(true)
    }

    test("The dot product of two tuples") {
        val a = vector(1, 2, 3)
        val b = vector(2, 3, 4)

        a.dot(b).shouldBe(20.0)
    }

    test("The cross product of two vectors") {
        val a = vector(1, 2, 3)
        val b = vector(2, 3, 4)

        a.cross(b).shouldBe(vector(-1, 2, -1))
        b.cross(a).shouldBe(vector(1, -2, 1))
    }

    test("Reflecting a vector approaching at 45º") {
        val v = vector(1, -1, 0)
        val n = vector(0, 1, 0)

        val r = v.reflect(n)

        r.shouldBe(vector(1, 1, 0))
    }

    test("Reflecting a vector of a slanted surface") {
        val v = vector(0, -1, 0)
        val n = vector(sqrt(2.0) / 2, sqrt(2.0) / 2, 0)

        val r = v.reflect(n)

        r.shouldBe(vector(1, 0, 0))
    }
})