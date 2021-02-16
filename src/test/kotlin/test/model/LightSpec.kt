package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.PointLight
import krater.model.AreaLight
import krater.model.Material
import krater.model.pattern.Stripe
import krater.model.shapes.Sphere
import test.model.shapes.TestShape
import kotlin.math.sqrt

class LightSpec : FunSpec({
    val noJitter = {  0.5 }
    val shape = TestShape(material = Material())
    val origin = point(0, 0, 0)
    val unshadowed = { _: Tuple, _: Tuple -> false }
    val shadowed = { _: Tuple, _: Tuple -> true }

    test("A point light has a position and intensity") {
        val color = Color(1.0, 1.0, 1.0)
        val position = point(0, 0, 0)

        val light = PointLight(position, color)

        light.position.shouldBe(position)
        light.color.shouldBe(color)
    }

    test("A point light has one sample, the same as its position") {
        val position = point(1, 2, 3)

        val light = PointLight(position, WHITE)

        light.samples.shouldBe(listOf(position))
    }

    test("Calculate intensity at a point derived from light for a PointLight") {
        val light = PointLight(point(0, 0, 0), WHITE)

        light.intensityAt(point(1, 0, 0), shadowed).shouldBe(0.0)
        light.intensityAt(point(1, 0, 0), unshadowed).shouldBe(1.0)
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

    test("An area light has a sample for each cell") {
        val corner = point(0, 0, 0)
        val v1 = vector(2, 0, 0)
        val v2 = vector(0, 0, 2)
        val light = AreaLight(corner, v1, 2, v2, 2, WHITE,
            jitter = repeat(listOf(0.0))
        )

        light.samples.shouldContainExactlyInAnyOrder(listOf(
            point(0, 0, 0),
            point(0, 0, 1),
            point(1, 0, 0),
            point(1, 0, 1)
        ))
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
            row(point(1.5, 0, 2), 1.0),  // Not as per text, but seems right by hand calculation
            row(point(1.25, 1.25, 3), 0.75),
            row(point(0, 0, -2), 1.0),
        ).forAll { point, result ->
            val light = AreaLight(corner, v1, 2, v2, 2, WHITE,
                jitter = repeat(listOf(0.7, 0.3, 0.9, 0.1, 0.5))
            )
            light.intensityAt(point, shadowEvaluator).shouldBe(result)
        }
    }

    test("Lighting with the eye between light and surface") {
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)

        light.lighting(shape, origin, eyev, normalv, unshadowed).shouldBe(Color(1.9, 1.9, 1.9))    }

    test("Lighting with the eye between light and surface, eye offset 45º") {
        val eyev =  vector(0, sqrt(2.0) /2, -sqrt(2.0) /2)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))

        light.lighting(shape, origin, eyev, normalv, unshadowed).shouldBe(Color(1.0, 1.0, 1.0))
    }

    test("Lighting with the eye opposite surface, light offset 45º") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 10, -10), Color(1.0, 1.0, 1.0))

        light.lighting(shape, origin, eyev, normalv, unshadowed).shouldBe(Color(0.7364, 0.7364, 0.7364))
    }

    test("Lighting with the eye in the path of the reflection vector") {
        val eyev =  vector(0, -sqrt(2.0) /2, -sqrt(2.0) /2)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 10, -10), Color(1.0, 1.0, 1.0))

        light.lighting(shape, origin, eyev, normalv, unshadowed).shouldBe(Color(1.6364, 1.6364, 1.6364))
    }

    test("Lighting with the light behind the surface") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, 10), Color(1.0, 1.0, 1.0))

        light.lighting(shape, origin, eyev, normalv, unshadowed).shouldBe(Color(0.1, 0.1, 0.1))
    }

    test("Lighting with the surface in shadow") {
        val eyev =  vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), Color(1.0, 1.0, 1.0))

        light.lighting(shape, origin, eyev, normalv, shadowed).shouldBe(Color(0.1, 0.1, 0.1))
    }

    test("Lighting with a pattern applied") {
        val s = TestShape(
            material = Material(
                ambient = 1.0,
                diffuse = 0.0,
                specular = 0.0,
                pattern = Stripe(WHITE, BLACK),
                reflective = 0.0,
            )
        )
        val eyev = vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), WHITE)

        val c1 = light.lighting(s, point(0.9, 0, 0), eyev, normalv, unshadowed)
        val c2 = light.lighting(s, point(1.1, 0, 0), eyev, normalv, unshadowed)

        c1.shouldBe(WHITE)
        c2.shouldBe(BLACK)
    }

    test("Lighting with a pattern applied and shape transformed") {
        val s = TestShape(
            material = Material(
                pattern = Stripe(WHITE, BLACK),
                ambient = 1.0,
                diffuse = 0.0,
                specular = 0.0,
            ),
            transform = translation(0.5, 0, 0)
        )

        val eyev = vector(0, 0, -1)
        val normalv = vector(0, 0, -1)
        val light = PointLight(point(0, 0, -10), WHITE)

        val c1 = light.lighting(s, point(1.4, 0, 0), eyev, normalv, unshadowed)
        val c2 = light.lighting(s, point(1.6, 0, 0), eyev, normalv, unshadowed)

        c1.shouldBe(WHITE)
        c2.shouldBe(BLACK)
    }
    test("Phong shading samples the area light") {
        val corner = point(-0.5, -0.5, -5)
        val v1 = vector(1, 0, 0)
        val v2 = vector(0, 1, 0)
        val light = AreaLight(corner, v1, 2, v2, 2, WHITE, jitter = { 0.5 })
        val sphere = Sphere(
            material = Material(ambient = 0.1, diffuse = 0.9, specular = 0.0, color = WHITE)
        )
        val eye = point(0, 0, -5)
        table(
            headers("point", "result"),
            row(point(0, 0, -1), Color(0.9965, 0.9965, 0.9965)),
            row(point(0, 0.7071, -0.7071), Color(0.62318, 0.62318, 0.62318)),
        ).forAll { point, result ->
            val eyev = (eye - point).normalize()
            val normalv = vector(point.x, point.y, point.z)
            light.lighting(sphere, point, eyev, normalv, unshadowed).shouldBe(result)
        }
    }
})

