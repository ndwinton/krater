package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.point
import krater.model.pattern.AlignCheck
import krater.model.pattern.CubeFace
import krater.model.pattern.MappedCube

class MappedCubeSpec : FunSpec({

    test("Identifying the face of a cube from a point") {
        table(
            headers("point", "face"),
            row(point(-1, 0.5, -0.25), CubeFace.LEFT),
            row(point(1.1, -0.75, 0.8), CubeFace.RIGHT),
            row(point(0.1, 0.6, 0.9), CubeFace.FRONT),
            row(point(-0.7, 0, -2), CubeFace.BACK),
            row(point(0.5, 1, 0.9), CubeFace.UP),
            row(point(-0.2, -1.3, 1.1), CubeFace.DOWN),
        ).forAll { point, face ->
            MappedCube.faceFromPoint(point).shouldBe(face)
        }
    }

    test("UV mapping the front face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(-0.5, 0.5, 1), 0.25, 0.75),
            row(point(0.5, -0.5, 1), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = MappedCube.uvFront(point)
            uv.u.shouldBe(u)
            uv.v.shouldBe(v)
        }
    }

    test("UV mapping the back face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(0.5, 0.5, -1), 0.25, 0.75),
            row(point(-0.5, -0.5, -1), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = MappedCube.uvBack(point)
            uv.u.shouldBe(u)
            uv.v.shouldBe(v)
        }
    }

    test("UV mapping the left face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(-1, 0.5, -0.5), 0.25, 0.75),
            row(point(-1, -0.5, 0.5), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = MappedCube.uvLeft(point)
            uv.u.shouldBe(u)
            uv.v.shouldBe(v)
        }
    }

    test("UV mapping the right face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(1, 0.5, 0.5), 0.25, 0.75),
            row(point(1, -0.5, -0.5), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = MappedCube.uvRight(point)
            uv.u.shouldBe(u)
            uv.v.shouldBe(v)
        }
    }

    test("UV mapping the upper face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(-0.5, 1, -0.5), 0.25, 0.75),
            row(point(0.5, 1, 0.5), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = MappedCube.uvUp(point)
            uv.u.shouldBe(u)
            uv.v.shouldBe(v)
        }
    }

    test("UV mapping the lower face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(-0.5, -1, 0.5), 0.25, 0.75),
            row(point(0.5, -1, -0.5), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = MappedCube.uvDown(point)
            uv.u.shouldBe(u)
            uv.v.shouldBe(v)
        }
    }

    test("Finding the colours on a mapped cube") {
        val red = Color(1.0, 0.0, 0.0)
        val green = Color(0.0, 1.0, 0.0)
        val blue = Color(0.0, 0.0, 1.0)
        val cyan = Color(0.0, 1.0, 1.0)
        val purple = Color(1.0, 0.0, 1.0)
        val yellow = Color(1.0, 1.0, 0.0)
        val brown = Color(1.0, 0.5, 0.0)
        val white = WHITE

        val left = AlignCheck(yellow, cyan, red, blue, brown)
        val front = AlignCheck(cyan, red, yellow, brown, green)
        val right = AlignCheck(red, yellow, purple, green, white)
        val back = AlignCheck(green, purple, cyan, white, blue)
        val up = AlignCheck(brown, cyan, purple, red, yellow)
        val down = AlignCheck(purple, brown, green, blue, white)

        val pattern = MappedCube(left, front, right, back, up, down)
        table(
            headers("point", "color"),
            row(point(-1, 0, 0), yellow),
            row(point(-1, 0.9, -0.9), cyan),
            row(point(-1, 0.9, 0.9), red),
            row(point(-1, -0.9, -0.9), blue),
            row(point(-1, -0.9, 0.9), brown),
            row(point(0, 0, 1), cyan),
            row(point(-0.9, 0.9, 1), red),
            row(point(0.9, 0.9, 1), yellow),
            row(point(-0.9, -0.9, 1), brown),
            row(point(0.9, -0.9, 1), green),
            row(point(1, 0, 0), red),
            row(point(1, 0.9, 0.9), yellow),
            row(point(1, 0.9, -0.9), purple),
            row(point(1, -0.9, 0.9), green),
            row(point(1, -0.9, -0.9), white),
            row(point(0, 0, -1), green),
            row(point(0.9, 0.9, -1), purple),
            row(point(-0.9, 0.9, -1), cyan),
            row(point(0.9, -0.9, -1), white),
            row(point(-0.9, -0.9, -1), blue),
            row(point(0, 1, 0), brown),
            row(point(-0.9, 1, -0.9), cyan),
            row(point(0.9, 1, -0.9), purple),
            row(point(-0.9, 1, 0.9), red),
            row(point(0.9, 1, 0.9), yellow),
            row(point(0, -1, 0), purple),
            row(point(-0.9, -1, 0.9), brown),
            row(point(0.9, -1, 0.9), green),
            row(point(-0.9, -1, -0.9), blue),
            row(point(0.9, -1, -0.9), white),
        ).forAll { point, color ->
            pattern.colorAt(point).shouldBe(color)
        }
    }
})