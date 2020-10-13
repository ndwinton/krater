package test.canvas

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import krater.canvas.Color

class ColorSpec : FunSpec({

    test("Colors are (red, green, blue) tuples") {
        val c = Color(-0.5, 0.4, 1.7)

        c.red.shouldBe(-0.5)
        c.green.shouldBe(0.4)
        c.blue.shouldBe(1.7)
    }

    test("Colors should compare equal within tolerance") {
        val c1 = Color(1.0, 2.0, -3.0)
        val c2 = Color(1.000005, 1.999995, -3.000009)
        val c3 = Color(1.0001, 2.0, -3.0001)

        (c1 == c2).shouldBe(true)
        (c1 == c3).shouldBe(false)
    }

    test("Colors should hash equal within tolerance") {
        val c1 = Color(1.0, 2.0, -3.0)
        val c2 = Color(1.000005, 1.999995, -3.000009)
        val c3 = Color(1.0001, 2.0, -3.0001)

        c1.hashCode().shouldBe(c2.hashCode())
        c1.hashCode().shouldNotBe(c3.hashCode())
    }

    test("Adding colors") {
        val c1 = Color(0.9, 0.6, 0.75)
        val c2 = Color(0.7, 0.1, 0.25)

        (c1 + c2).shouldBe(Color(1.6, 0.7, 1.0))
    }

    test("Subtracting colors") {
        val c1 = Color(0.9, 0.6, 0.75)
        val c2 = Color(0.7, 0.1, 0.25)

        (c1 - c2).shouldBe(Color(0.2, 0.5, 0.5))
    }

    test("Multiplying a color by a scalar") {
        val c = Color(0.2, 0.3, 0.4)

        (c * 2.0).shouldBe(Color(0.4, 0.6, 0.8))
    }

    test("Multiplying colors") {
        val c1 = Color(1.0, 0.2, 0.4)
        val c2 = Color(0.9, 1.0, 0.1)

        (c1 * c2).shouldBe(Color(0.9, 0.2, 0.04))
    }

    test("Scaling to an integer triplet within bounds") {
        val c1 = Color(0.0, 0.5, 1.0)
        val c2 = Color(0.5, 2.0, -1.0)

        c1.toScaledTriple(255).shouldBe(Triple(0, 128, 255))
        c2.toScaledTriple(255).shouldBe(Triple(128, 255, 0))
    }
})