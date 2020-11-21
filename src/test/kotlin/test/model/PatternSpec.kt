package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.WHITE
import krater.geometry.point
import krater.geometry.scaling
import krater.geometry.translation
import krater.model.Sphere
import krater.model.StripePattern

class PatternSpec : FunSpec ({

    test("Creating a stripe pattern") {
        val pattern = StripePattern(WHITE, BLACK)

        pattern.a.shouldBe(WHITE)
        pattern.b.shouldBe(BLACK)
    }

    test("A stripe pattern is constant in y") {
        val pattern = StripePattern(WHITE, BLACK)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0.5, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 1, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 1.5, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 2, 0)).shouldBe(WHITE)
    }

    test("A stripe pattern is constant in z") {
        val pattern = StripePattern(WHITE, BLACK)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0, 0.5)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0, 1)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0, 1.5)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0, 2)).shouldBe(WHITE)
    }

    test("A stripe alternates in x") {
        val pattern = StripePattern(WHITE, BLACK)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0.9, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(1, 0, 0)).shouldBe(BLACK)
        pattern.colorAt(point(-0.1, 0, 0)).shouldBe(BLACK)
        pattern.colorAt(point(-1, 0, 0)).shouldBe(BLACK)
        pattern.colorAt(point(-1.1, 0, 0)).shouldBe(WHITE)
    }

    test("Stripes with a pattern transformation") {
        val pattern = StripePattern(WHITE, BLACK, transform = translation(0.5, 0, 0))

        val c = pattern.colorAtObject(point(2.5, 0, 0))

        c.shouldBe(WHITE)
    }
})