package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.model.Material
import krater.model.PointLight
import krater.geometry.point
import krater.geometry.vector
import krater.model.pattern.Stripe
import kotlin.math.sqrt

class MaterialSpec : FunSpec({

    test("The default material") {
        Material().apply {
            color.shouldBe(Color(1.0, 1.0, 1.0))
            ambient.shouldBe(0.1)
            diffuse.shouldBe(0.9)
            specular.shouldBe(0.9)
            shininess.shouldBe(200.0)
        }
    }

    test("Reflectivity for the default material") {
        val m = Material()

        m.reflective.shouldBe(0.0)
    }

    test("Transparency and refractive index for default material") {
        val m = Material()

        m.transparency.shouldBe(0.0)
        m.refractiveIndex.shouldBe(1.0)
    }

    test("Shadow property defaults to true") {

        val m = Material()

        m.shadow.shouldBe(true)
    }
})