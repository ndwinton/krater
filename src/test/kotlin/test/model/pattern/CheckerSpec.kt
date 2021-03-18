package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.WHITE
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.point
import krater.model.pattern.Checker
import krater.model.pattern.map.UVPoint

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

    test("Checkers should support U/V frequency") {
        val checkers = Checker(a = BLACK, b = WHITE, uFrequency = 2, vFrequency = 2)
        table(
            headers("u", "v", "expected"),
            row(0.0, 0.0, BLACK),
            row(0.5, 0.0, WHITE),
            row(0.0, 0.5, WHITE),
            row(0.5, 0.5, BLACK),
            row(1.0, 1.0, BLACK),

            ).forAll { u, v, expected ->
            checkers.colorAtUV(UVPoint(u, v)).shouldBe(expected)
        }
    }
})