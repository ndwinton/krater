package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.point
import krater.geometry.scaling
import krater.geometry.vector
import krater.model.*

class WorldSpec : FunSpec ({
    val light1 = PointLight(point(-10, 10, -10), WHITE)
    val light2 = PointLight(point(10, 10, -10), WHITE)
    val defaultWorld = World(
        lights = listOf(light1),
        objects = listOf(
            Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
            Sphere(transform = scaling(0.5, 0.5, 0.5))
        )
    )

    test("Creating a world") {
        val w = World()

        w.objects.shouldBeEmpty()
        w.lights.shouldBe(listOf(DARKNESS))
    }

    test("The default world") {
        val light = PointLight(point(-10, 10, -10), WHITE)
        val material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = material)
        val s2 = Sphere(transform = scaling(0.5, 0.5, 0.5))

        defaultWorld.lights.shouldBe(listOf(light))
        defaultWorld.objects.shouldContainAll(s1, s2)
    }

    test("Intersect a world with a ray") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))

        val xs = defaultWorld.intersect(r)

        xs.size.shouldBe(4)
        xs.map { it.t }.shouldBe(listOf(4.0, 4.5, 5.5, 6.0))
    }

    test("Shading an intersection") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val shape = defaultWorld.objects[0]
        val i = Intersection(4.0, shape)

        val comps = PreparedComputation(i, r)
        val c = defaultWorld.shadeHit(comps)

        c.shouldBe(Color(0.38066, 0.47583, 0.2855))
    }

    test("Shading an intersection, multiple lights") {
        val w = World(defaultWorld, lights = listOf(light1, light2))
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val shape = w.objects[0]
        val i = Intersection(4.0, shape)

        val comps = PreparedComputation(i, r)
        val c = w.shadeHit(comps)

        c.shouldBe(Color(0.76132, 0.95166, 0.5710))
    }

    test("Shading an intersection from the inside") {
        val w = World(defaultWorld, lights = listOf(PointLight(point(0, 0.25, 0), WHITE)))
        val r = Ray(point(0, 0, 0), vector(0, 0, 1))
        val shape = w.objects[1]
        val i = Intersection(0.5, shape)

        val comps = PreparedComputation(i, r)
        val c = w.shadeHit(comps)

        c.shouldBe(Color(0.90498, 0.90498, 0.90498))
    }

    test("Shading an intersection from the inside, multiple lights") {
        val w = World(defaultWorld, lights = listOf(PointLight(point(0, 0.25, 0), WHITE), PointLight(point(0, -0.25, 0), WHITE)))
        val r = Ray(point(0, 0, 0), vector(0, 0, 1))
        val shape = w.objects[1]
        val i = Intersection(0.5, shape)

        val comps = PreparedComputation(i, r)
        val c = w.shadeHit(comps)

        c.shouldBe(Color(1.80996, 1.80996, 1.80996))
    }

    test("The color when a ray misses") {
        val r = Ray(point(0, 0, -5), vector(0, 1, 0))

        val c = defaultWorld.colorAt(r)

        c.shouldBe(BLACK)
    }

    test("The color when a ray hits") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))

        val c = defaultWorld.colorAt(r)

        c.shouldBe(Color(0.38066, 0.47583, 0.2855))
    }

    test("The color with an intersection behind the ray") {
        val w = World(
            lights = listOf(PointLight(point(-10, 10, -10), WHITE)),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2, ambient = 1.0)),
                Sphere(transform = scaling(0.5, 0.5, 0.5), Material(ambient = 1.0))
            )
        )
        val inner = w.objects.last()
        val r = Ray(point(0, 0, 0.75), vector(0, 0, -1))

        val c = w.colorAt(r)

        c.shouldBe(inner.material.color)
    }

    test("The color when a ray hits, with multiple lights") {
        val dualLights = World(
            lights = listOf(PointLight(point(-10, 10, -10), WHITE), PointLight(point(10, 10, -10), WHITE)),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
                Sphere(transform = scaling(0.5, 0.5, 0.5))
            )
        )

        val r = Ray(point(0, 0, -5), vector(0, 0, 1))

        val c = dualLights.colorAt(r)

        c.shouldBe(Color(0.76132, 0.95166, 0.5710))
    }

    test("The color with multiple lights, intersection behind the ray") {
        val w = World(
            lights = listOf(PointLight(point(-10, 10, -10), WHITE), PointLight(point(10, 10, -10), WHITE)),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2, ambient = 1.0)),
                Sphere(transform = scaling(0.5, 0.5, 0.5), Material(ambient = 1.0))
            )
        )
        val inner = w.objects.last()
        val r = Ray(point(0, 0, 0.75), vector(0, 0, -1))

        val c = w.colorAt(r)

        c.shouldBe(inner.material.color + inner.material.color)
    }

})