package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.pattern.Checker
import krater.model.pattern.Pattern
import krater.model.pattern.Solid
import krater.model.pattern.Stripe
import krater.model.pattern.map.UVPoint
import kotlin.math.PI

class PatternSpec : FunSpec({
    class TestPatten(transform: Matrix) : Pattern(transform = transform) {
        override fun colorAt(point: Tuple): Color = Color(point.x, point.y, point.z)
    }

    test("A pattern with a transformation") {
        val pattern = TestPatten(transform = scaling(2, 2, 2))

        val c = pattern.colorAtObject(point(2, 3, 4))

        c.shouldBe(Color(1.0, 1.5, 2.0))
    }

    test("Nesting patterns") {

        val pattern = Checker(
            Stripe(Color(1.0, 0.0, 0.0), Color(1.0, 0.5, 0.5), rotationY(PI /2).scale(0.25, 1, 0.25), 2),
            Stripe(Color(0.0, 0.0, 1.0), Color(0.5, 0.5, 1.0), scaling(0.25, 1, 0.25), 2),
            transform = translation(0, -EPSILON, 0)
        )

        pattern.colorAtObject(point(0.125, 0, 0.125)).shouldBe(Color(1.0, 0.5, 0.5))
        pattern.colorAtObject(point(0.125, 0, 0.375)).shouldBe(Color(1.0, 0.0, 0.0))
        pattern.colorAtObject(point(1.125, 0, 0.375)).shouldBe(Color(0.0, 0.0, 1.0))
        pattern.colorAtObject(point(1.375, 0, 0.375)).shouldBe(Color(0.5, 0.5, 1.0))
    }

    test("Adding patterns gives new ColorProvider with average of values") {
        val p1 = Solid(Color(0.5, 0.4, 0.3))
        val p2 = Solid(Color(0.5, 0.6, 0.7))
        val p3 = Solid(Color(1.5, 0.0, 0.7))

        (p1 + p2).colorAtObject(point(0, 0, 0)).shouldBe(Color(0.5, 0.5, 0.5))
        (p1 + p3).colorAtObject(point(0, 0, 0)).shouldBe(Color(1.0, 0.2, 0.5))
    }

    test("Subtracting patterns gives new ColorProvider with absolute difference of values") {
        val p1 = Solid(Color(0.5, 0.4, 0.3))
        val p2 = Solid(Color(0.5, 0.6, 0.7))
        val p3 = Solid(Color(1.5, 0.0, 0.7))

        (p1 - p2).colorAtObject(point(0, 0, 0)).shouldBe(Color(0.0, 0.2, 0.4))
        (p3 - p1).colorAtObject(point(0, 0, 0)).shouldBe(Color(1.0, 0.4, 0.4))
    }

    test("Patterns should map u/v points to x/z by default") {
        val solid = Solid(BLACK)
        val stripe = Stripe(BLACK, WHITE, repeat = 2)

        solid.colorAtUV(UVPoint(1.0, 3.0)).shouldBe(BLACK)
        stripe.colorAtUV(UVPoint(0.5, 0.5)).shouldBe(BLACK)
        stripe.colorAtUV(UVPoint(1.5, 1.5)).shouldBe(WHITE)
    }
})