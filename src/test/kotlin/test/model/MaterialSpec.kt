package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.model.Material
import krater.model.PointLight
import krater.geometry.point
import krater.geometry.vector
import kotlin.math.sqrt

class MaterialSpec : FunSpec({
    val m = Material()
    val position = point(0, 0, 0)

    test("The default material") {
        Material().apply {
            color.shouldBe(Color(1.0, 1.0, 1.0))
            ambient.shouldBe(0.1)
            diffuse.shouldBe(0.9)
            specular.shouldBe(0.9)
            shininess.shouldBe(200.0)
        }
    }

    test("Lighting with the eye between light and surface") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))

        m.lighting(light, position, eyev, normalv).shouldBe(Color(1.9, 1.9, 1.9))
    }

    test("Lighting with the eye between light and surface, eye offset 45º") {
        val eyev =  vector(0, sqrt(2.0) /2, -sqrt(2.0)/2)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))

        m.lighting(light, position, eyev, normalv).shouldBe(Color(1.0, 1.0, 1.0))
    }

    test("Lighting with the eye opposite surface, light offset 45º") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 10, -10), Color(1.0, 1.0, 1.0))

        m.lighting(light, position, eyev, normalv).shouldBe(Color(0.7364, 0.7364, 0.7364))
    }

    test("Lighting with the eye in the path of the reflection vector") {
        val eyev =  vector(0, -sqrt(2.0) /2, -sqrt(2.0)/2)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 10, -10), Color(1.0, 1.0, 1.0))

        m.lighting(light, position, eyev, normalv).shouldBe(Color(1.6364, 1.6364, 1.6364))
    }

    test("Lighting with the light behind the surface") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, 10), Color(1.0, 1.0, 1.0))

        m.lighting(light, position, eyev, normalv).shouldBe(Color(0.1, 0.1, 0.1))
    }
})