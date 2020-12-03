package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.ColorProvider
import krater.canvas.WHITE
import krater.geometry.Tuple
import krater.geometry.point
import krater.model.pattern.Pattern
import krater.model.pattern.noise.NoiseProvider

class NoiseProviderSpec : FunSpec ({

    class TestNoise(pattern: ColorProvider) : NoiseProvider(pattern) {
        override fun perturb(point: Tuple): Tuple {
            return point(point.z, point.x, point.y)
        }
    }

    class TestPattern : Pattern() {
        var savedPoint = point(0, 0, 0)
        override fun colorAt(point: Tuple): Color {
            savedPoint = point
            return WHITE
        }
    }

    test("Noise should perturb patterns") {

        val testPattern = TestPattern()
        val pattern = TestNoise(testPattern)

        pattern.colorAt(point(1, 2, 3)).shouldBe(WHITE)
        testPattern.savedPoint.shouldBe(point(3, 1, 2))
    }

})