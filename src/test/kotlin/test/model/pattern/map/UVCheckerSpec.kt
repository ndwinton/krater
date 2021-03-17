package test.model.pattern.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.WHITE
import krater.model.pattern.map.UVChecker
import krater.model.pattern.map.UVPoint

class UVCheckerSpec : FunSpec({
    test("Checker pattern in 2D") {
        val checkers = UVChecker(2, 2, BLACK, WHITE)
        table(
            headers("u", "v", "expected"),
            row(0.0, 0.0, BLACK),
            row(0.5, 0.0, WHITE),
            row(0.0, 0.5, WHITE),
            row(0.5, 0.5, BLACK),
            row(1.0, 1.0, BLACK),

        ).forAll { u, v, expected ->
            checkers.uvColorAt(UVPoint(u, v)).shouldBe(expected)
        }
    }
})