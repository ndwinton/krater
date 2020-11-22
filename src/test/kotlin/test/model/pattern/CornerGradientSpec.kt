package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.point
import krater.model.pattern.CornerGradient
import kotlin.math.sqrt

class CornerGradientSpec : FunSpec({
    test("A corner gradient linearly interpolates between colors in two dimensions") {
        val pattern = CornerGradient(WHITE, BLACK, IDENTITY_4X4_MATRIX)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(0.25, 0, 0)).shouldBe(Color(0.75, 0.75, 0.75))
        pattern.colorAt(point(0.5, 0, 0)).shouldBe(Color(0.5, 0.5, 0.5))
        pattern.colorAt(point(0.75, 0, 0)).shouldBe(Color(0.25, 0.25, 0.25))

        pattern.colorAt(point(0, 0, 0.25)).shouldBe(Color(0.75, 0.75, 0.75))
        pattern.colorAt(point(0, 0, 0.5)).shouldBe(Color(0.5, 0.5, 0.5))
        pattern.colorAt(point(0, 0, 0.75)).shouldBe(Color(0.25, 0.25, 0.25))

        pattern.colorAt(point(0.5 / sqrt(2.0), 0, 0.5 / sqrt(2.0))).shouldBe(Color(0.5, 0.5, 0.5))
    }
})