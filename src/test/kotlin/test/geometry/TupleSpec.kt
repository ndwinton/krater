package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldNotBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import krater.geometry.*

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
})