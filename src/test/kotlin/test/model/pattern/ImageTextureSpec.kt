package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.model.pattern.ImageTexture
import krater.model.pattern.map.UVPoint
import java.io.File

class ImageTextureSpec : FunSpec({
    test("Reading gradient pattern from a PNG file") {
        val texture = ImageTexture(file = File("src/test/resources/gradient.png"))
        table(
            headers("u", "v", "expected"),
            row(0.0, 0.0, Color(0.9, 0.9, 0.9)),
            row(0.35, 0.0, Color(0.2, 0.2, 0.2)),
            row(0.65, 0.45, Color(0.1, 0.1, 0.1)),
            row(1.0, 1.0, Color(0.9, 0.9, 0.9)),
        ).forAll { u, v, expected ->
            texture.colorAtUV(UVPoint(u, v)).shouldBe(expected)
        }
    }
})