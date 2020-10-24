package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.geometry.PointLight
import krater.geometry.point

class LightSpec : FunSpec({
    test("A point light hasa a position and intensity") {
        val intensity = Color(1.0, 1.0, 1.0)
        val position = point(0, 0, 0)

        val light = PointLight(position, intensity)

        light.position.shouldBe(position)
        light.intensity.shouldBe(intensity)
    }
})