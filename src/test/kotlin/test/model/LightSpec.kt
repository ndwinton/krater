package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.canvas.Color
import krater.canvas.WHITE
import krater.model.PointLight
import krater.geometry.point
import krater.geometry.vector
import krater.model.AreaLight

class LightSpec : FunSpec({
    val noJitter = {  0.5 }

    test("A point light has a position and intensity") {
        val color = Color(1.0, 1.0, 1.0)
        val position = point(0, 0, 0)

        val light = PointLight(position, color)

        light.position.shouldBe(position)
        light.color.shouldBe(color)
    }

    test("Calculate intensity at a point derived from light for a PointLight") {
        val light = PointLight(point(0, 0, 0), WHITE)

        light.intensityAt(point(1, 0, 0), { _, _ -> true }).shouldBe(0.0)
        light.intensityAt(point(1, 0, 0), { _, _ -> false }).shouldBe(1.0)
    }

    test("Creating an AreaLight") {
        val corner = point(1, 1, 1)
        val v1 = vector(2, 0, 0)
        val v2 = vector(0, 0, 1)

        val light = AreaLight(corner, v1, 4, v2, 2, WHITE)

        light.corner.shouldBe(corner)
        light.uVec.shouldBe(vector(0.5, 0, 0))
        light.uSteps.shouldBe(4)
        light.vVec.shouldBe(vector(0, 0, 0.5))
        light.vSteps.shouldBe(2)
        light.position.shouldBe(point(2, 1, 1.5))
    }

    test("Finding a single point on an area light") {
        val corner = point(1, 1, 1)
        val v1 = vector(2, 0, 0)
        val v2 = vector(0, 0, 1)
        val light = AreaLight(corner, v1, 4, v2, 2, WHITE, jitter = noJitter)

        table(
            headers("u", "v", "result"),
            row(0, 0, point(1.25, 1, 1.25)),
            row(1, 0, point(1.75, 1, 1.25)),
            row(0, 1, point(1.25, 1, 1.75)),
            row(2, 0, point(2.25, 1, 1.25)),
            row(3, 1, point(2.75, 1, 1.75)),
        ).forAll { u, v, result ->
            light.cellPoint(u, v).shouldBe(result)
        }
    }

    test("The area light intensity function") {
        val corner = point(-0.5, -0.5, -5)
        val v1 = vector(1, 0, 0)
        val v2 = vector(0, 1, 0)
        val shadowEvaluator = defaultWorld::isShadowed
        val light = AreaLight(corner, v1, 2, v2, 2, WHITE, jitter = noJitter)

        table(
            headers("point", "result"),
            row(point(0, 0, 2), 0.0),
            row(point(1, -1, 2), 0.25),
            row(point(1.5, 0, 2), 0.5),
            row(point(1.25, 1.25, 3), 0.75),
            row(point(0, 0, -2), 1.0),
        ).forAll { point, result ->
            light.intensityAt(point, shadowEvaluator).shouldBe(result)
        }
    }

    fun repeat(list: List<Double>): () -> Double {
        var index = 0
        return {
            val next = list[index]
            index = (index + 1) % list.size
            next
        }
    }

    test("Finding a single point on a jittered area light") {
        val corner = point(0, 0, 0)
        val v1 = vector(2, 0, 0)
        val v2 = vector(0, 0, 1)

        table(
            headers("u", "v", "result"),
            row(0, 0, point(0.15, 0, 0.35)),
            row(1, 0, point(0.65, 0, 0.35)),
            row(0, 1, point(0.15, 0, 0.85)),
            row(2, 0, point(1.15, 0, 0.35)),
            row(3, 1, point(1.65, 0, 0.85)),
        ).forAll { u, v, result ->
            val light = AreaLight(corner, v1, 4, v2, 2, WHITE,
                jitter = repeat(listOf(0.3, 0.7))
            )
            light.cellPoint(u, v).shouldBe(result)
        }
    }

    test("The area light with jittered samples") {
        val corner = point(-0.5, -0.5, -5)
        val v1 = vector(1, 0, 0)
        val v2 = vector(0, 1, 0)
        val shadowEvaluator = defaultWorld::isShadowed

        table(
            headers("point", "result"),
            row(point(0, 0, 2), 0.0),
            row(point(1, -1, 2), 0.5),
            row(point(1.5, 0, 2), 0.75),
            row(point(1.25, 1.25, 3), 0.75),
            row(point(0, 0, -2), 1.0),
        ).forAll { point, result ->
            val light = AreaLight(corner, v1, 2, v2, 2, WHITE,
                jitter = repeat(listOf(0.7, 0.3, 0.9, 0.1, 0.5))
            )
            light.intensityAt(point, shadowEvaluator).shouldBe(result)
        }
    }

})

