package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.model.pattern.AlignCheck
import krater.model.pattern.map.UVPoint

class AlignCheckSpec : FunSpec({
    val red = Color(1.0, 0.0, 0.0)
    val green = Color(0.0, 1.0, 0.0)
    val cyan = Color(0.0, 1.0, 1.0)
    val yellow = Color(1.0, 1.0, 0.0)
    val white = WHITE

    test("Layout of the 'align check' pattern") {
        val main = white
        val ul = red
        val ur = yellow
        val bl = green
        val br = cyan
        val pattern = AlignCheck(main = main, ul = ul, ur = ur, bl = bl, br = br)
        table(
            headers("u", "v", "expected"),
            row(0.5, 0.5, main),
            row(0.1, 0.9, ul),
            row(0.1, 0.1, bl),
            row(0.9, 0.9, ur),
            row(0.9, 0.1, br),
        ).forAll { u, v, expected ->
            pattern.colorAtUV(UVPoint(u, v)).shouldBe(expected)
        }
    }
})