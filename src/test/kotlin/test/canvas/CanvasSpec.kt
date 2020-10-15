package test.canvas

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import krater.canvas.Canvas
import krater.canvas.Color

class CanvasSpec : FunSpec({

    test("Creating a canvas") {
        val c = Canvas(10, 20)

        c.width.shouldBe(10)
        c.height.shouldBe(20)
        (0 .. 19).forEach { row ->
            (0 .. 9).forEach { col ->
                c[col, row].shouldBe(Color(0.0, 0.0, 0.0))
            }
        }
    }

    test("Writing pixels to a canvas") {
        val c = Canvas(10, 20)
        val red = Color(1.0, 0.0, 0.0)

        c[2, 3] = red

        c[2, 3].shouldBe(red)
    }

    test("Constructing the PPM header") {
        val c = Canvas(5, 3)

        val ppm = c.toPPM()

        ppm.split("\n")
            .subList(0, 3)
            .shouldBe(listOf("P3", "5 3", "255"))
    }

    test("Constructing the PPM header with other values") {
        val c = Canvas(10, 4)

        val ppm = c.toPPM()

        ppm.split("\n")
            .subList(0, 3)
            .shouldBe(listOf("P3", "10 4", "255"))
    }

    test("Constructing the PPM pixel data") {
        val c = Canvas(5, 3)
        val c1 = Color(1.5, 0.0, 0.0)
        val c2 = Color(0.0, 0.5, 0.0)
        val c3 = Color(-0.5, 0.0, 1.0)

        c[0, 0] = c1
        c[2, 1] = c2
        c[4, 2] = c3

        val ppm = c.toPPM()

        ppm.split("\n")
            .subList(3, 6)
            .shouldBe(listOf(
                "255 0 0 0 0 0 0 0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255"
            ))
    }

    test("Splitting long lines in PPM files") {
        val c = Canvas(10, 2)

        (0 .. 1).forEach { row ->
            (0 .. 9).forEach { col -> c[col, row] = Color(1.0, 0.8, 0.6) }
        }
        val ppm = c.toPPM()

        ppm.split("\n").subList(3, 7).shouldBe(listOf(
            "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204",
            "153 255 204 153 255 204 153 255 204 153 255 204 153",
            "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204",
            "153 255 204 153 255 204 153 255 204 153 255 204 153",
        ))
    }

    test("PPM files are terminated by a newline") {
        val c = Canvas(5, 3)

        val ppm = c.toPPM()

        ppm.shouldEndWith("\n")
    }
})