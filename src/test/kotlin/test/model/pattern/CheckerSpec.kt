package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.WHITE
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.point
import krater.model.pattern.Checker
import krater.model.pattern.Ring

class CheckerSpec : FunSpec({
    test("Checkers should repeat in x") {
        val pattern = Checker(WHITE, BLACK, IDENTITY_4X4_MATRIX)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0.99, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(1.01, 0, 0)).shouldBe(BLACK)
    }

    test("Checkers should repeat in y") {
        val pattern = Checker(WHITE, BLACK, IDENTITY_4X4_MATRIX)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0.99, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 1.01, 0)).shouldBe(BLACK)
    }

    test("Checkers should repeat in z") {
        val pattern = Checker(WHITE, BLACK, IDENTITY_4X4_MATRIX)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0, 0.99)).shouldBe(WHITE)
        pattern.colorAt(point(0, 0, 1.01)).shouldBe(BLACK)
    }
})