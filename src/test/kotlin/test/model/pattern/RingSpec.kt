package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.WHITE
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.point
import krater.model.pattern.Ring

class RingSpec : FunSpec({
    test("A ring should extend in both x and z") {
        val pattern = Ring(WHITE, BLACK, IDENTITY_4X4_MATRIX)

        pattern.colorAt(point(0, 0, 0)).shouldBe(WHITE)
        pattern.colorAt(point(1, 0, 0)).shouldBe(BLACK)
        pattern.colorAt(point(0, 0, 1)).shouldBe(BLACK)
        pattern.colorAt(point(0.708, 0, 0.708)).shouldBe(BLACK)
    }
})