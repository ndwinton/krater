package test.model.pattern.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.near
import krater.geometry.point
import krater.model.pattern.AlignCheck
import krater.model.pattern.map.*
import kotlin.math.sqrt

class MappingSpec : FunSpec({

    test("Using a spherical mapping onto a 3D point") {
        table(
            headers("point", "u", "v"),
            row(point(0, 0, -1), 0.0, 0.5),
            row(point(1, 0, 0), 0.25, 0.5),
            row(point(0, 0, 1), 0.5, 0.5),
            row(point(-1, 0, 0), 0.75, 0.5),
            row(point(0, 1, 0), 0.5, 1.0),
            row(point(0, -1, 0), 0.5, 0.0),
            row(point(sqrt(2.0) / 2.0, sqrt(2.0) / 2.0, 0), 0.25, 0.75),
        ).forAll { point, u, v ->
            val uvPoint = SphericalMap.map(point)
            uvPoint.u.shouldBe(u)
            uvPoint.v.shouldBe(v)
        }
    }

    test("Using a planar mapping onto a 3D point") {
        table(
            headers("point", "u", "v"),
            row(point(0.25, 0, 0.5), 0.25, 0.5),
            row(point(0.25, 0, -0.25), 0.25, -0.25),
            row(point(0.25, 0.5, -0.25), 0.25, -0.25),
            row(point(1.25, 0, 0.5), 0.25, 0.5),
            row(point(0.25, 0, -1.75), 0.25, -0.75),
            row(point(1, 0, -1), 0.0, 0.0),
            row(point(0, 0, 0), 0.0, 0.0),
        ).forAll { point, u, v ->
            val uv = PlanarMap.map(point)
            uv.u.near(u).shouldBe(true)
            uv.v.near(v).shouldBe(true)
        }
    }

    test("Using a cylindrical mapping on a 3D point") {
        table(
            headers("point", "u", "v"),
            row(point(0, 0, -1), 0.0, 0.0),
            row(point(0, 0.5, -1), 0.0, 0.5),
            row(point(0, 1, -1), 0.0, 0.0),
            row(point(0.70711, 0.5, -0.70711), 0.125, 0.5),
            row(point(1, 0.5, 0), 0.25, 0.5),
            row(point(0.70711, 0.5, 0.70711), 0.375, 0.5),
            row(point(0, -0.25, 1), 0.5, -0.25),
            row(point(-0.70711, 0.5, 0.70711), 0.625, 0.5),
            row(point(-1, 1.25, 0), 0.75, 0.25),
            row(point(-0.70711, 0.5, -0.70711), 0.875, 0.5),
        ).forAll { point, u, v ->
            val uv = CylindricalMap.map(point)
            uv.u.near(u).shouldBe(true)
            uv.v.near(v).shouldBe(true)
        }
    }

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
            CubeMap.faceFromPoint(point).shouldBe(face)
        }
    }

    test("UV mapping the front face of a cube") {
        table(
            headers("point", "u", "v"),
            row(point(-0.5, 0.5, 1), 0.25, 0.75),
            row(point(0.5, -0.5, 1), 0.75, 0.25)
        ).forAll { point, u, v ->
            val uv = CubeMap.uvFront(point)
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
            val uv = CubeMap.uvBack(point)
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
            val uv = CubeMap.uvLeft(point)
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
            val uv = CubeMap.uvRight(point)
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
            val uv = CubeMap.uvUp(point)
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
            val uv = CubeMap.uvDown(point)
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

        val pattern = CubeMap(left, front, right, back, up, down)
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


