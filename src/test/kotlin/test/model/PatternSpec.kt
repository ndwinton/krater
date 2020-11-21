package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.point
import krater.geometry.scaling
import krater.model.Pattern

class PatternSpec : FunSpec({
    class TestPatten(transform: Matrix) : Pattern(transform = transform) {
        override fun colorAt(point: Tuple): Color = Color(point.x, point.y, point.z)
    }

    test("A pattern with a transformation") {
        val pattern = TestPatten(transform = scaling(2, 2, 2))

        val c = pattern.colorAtObject(point(2, 3, 4))

        c.shouldBe(Color(1.0, 1.5, 2.0))
    }
})