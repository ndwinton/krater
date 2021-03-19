package test.model.pattern

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.WHITE
import krater.geometry.point
import krater.model.pattern.Checker
import krater.model.pattern.map.SphericalMapping
import krater.model.pattern.MappedTexture

class MappedTextureSpec : FunSpec({

    test("Using a texture map pattern with a spherical map") {
        val checkers = Checker(uFrequency = 16, vFrequency = 8, a = BLACK, b = WHITE)
        val pattern = MappedTexture(checkers, SphericalMapping)
        table(
            headers("point", "color"),
            row(point(0.4315, 0.4670, 0.7719), WHITE),
            row(point(-0.9654, 0.2552, -0.0534), BLACK),
            row(point(0.1039, 0.7090, 0.6975), WHITE),
            row(point(-0.4986, -0.7856, -0.3663), BLACK),
            row(point(-0.0317, -0.9395, 0.3411), BLACK),
            row(point(0.4809, -0.7721, 0.4154), BLACK),
            row(point(0.0285, -0.9612, -0.2745), BLACK),
            row(point(-0.5734, -0.2162, -0.7903), WHITE),
            row(point(0.7688, -0.1470, 0.6223), BLACK),
            row(point(-0.7652, 0.2175, 0.6060), BLACK),
        ).forAll { point, color ->
            pattern.colorAt(point).shouldBe(color)
        }
    }
})