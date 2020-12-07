package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import krater.canvas.BLACK
import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.*
import krater.model.*
import kotlin.math.sqrt

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

    test("There is no shadow when nothing is collinear with point and light") {
        defaultWorld.isShadowed(light1, point(0, 10, 0)).shouldBe(false)
    }

    test("The shadow when an object is between the point and the light") {
        defaultWorld.isShadowed(light1, point(10, -10, 10)).shouldBe(true)
    }

    test("There is no shadow when an object is behind the light") {
        defaultWorld.isShadowed(light1, point(-20, 20, -20)).shouldBe(false)
    }

    test("There is no shadow when an object is behind the point") {
        defaultWorld.isShadowed(light1, point(-2, 2, -2)).shouldBe(false)
    }

    test("shadeHit is given an intersection in shadow") {
        val s1 = Sphere()
        val s2 = Sphere(transform = translation(0, 0, 10))
        val w = World(
            lights = listOf(PointLight(point(0, 0, -10), WHITE)),
            objects = listOf(s1, s2)
        )
        val r = Ray(point(0, 0, 5), vector(0, 0, 1))
        val i = Intersection(4.0, s2)
        val comps = PreparedComputation(i, r)
        val c = w.shadeHit(comps)

        c.shouldBe(Color(0.1, 0.1, 0.1))
    }

    test("The hit should offset the point") {
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val shape = Sphere(translation(0, 0, 1))
        val i = Intersection(5.0, shape)
        val comps = PreparedComputation(i, r)

        comps.overPoint.z.shouldBeLessThan(-EPSILON / 2)
        comps.point.z.shouldBeGreaterThan(comps.overPoint.z)
    }

    test("shadeHit is given an intersection in shadow from one of multiple lights") {
        val s1 = Sphere()
        val s2 = Sphere(transform = translation(0, 0, 10))
        val w = World(
            lights = listOf(PointLight(point(0, 0, -10), WHITE), PointLight(point(0, 10, 5), WHITE)),
            objects = listOf(s1, s2)
        )
        val r = Ray(point(0, 0, 5), vector(0, 0, 1))
        val i = Intersection(4.0, s2)
        val comps = PreparedComputation(i, r)
        val c = w.shadeHit(comps)

        c.shouldBe(Color(0.53425, 0.53425, 0.53425))
    }

    test("The reflected color for a non-reflective material") {
        val w = World(
            lights = listOf(light1),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
                Sphere(transform = scaling(0.5, 0.5, 0.5), material = Material(ambient = 1.0))
            )
        )

        val r = Ray(point(0, 0, 0), vector(0, 0, 1))
        val shape = w.objects[1]
        val i = Intersection(1.0, shape)

        val comps = PreparedComputation(i, r)
        val color = w.reflectedColor(comps)

        color.shouldBe(BLACK)
    }

    test("The reflected color for a reflective material") {
        val shape = Plane(material = Material(reflective = 0.5), transform = translation(0, -1, 0))
        val w = World(
            lights = listOf(light1),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
                Sphere(transform = scaling(0.5, 0.5, 0.5), material = Material(ambient = 1.0)),
                shape
            )
        )
        val r = Ray(point(0, 0, -3), vector(0, -sqrt(2.0) / 2.0, sqrt(2.0) / 2.0))
        val i = Intersection(sqrt(2.0), shape)

        val comps = PreparedComputation(i, r)
        val color = w.reflectedColor(comps = comps)

        color.shouldBe(Color(0.19033, 0.23792, 0.14275))
    }

    test("shadeHit with a reflective material") {
        val shape = Plane(material = Material(reflective = 0.5), transform = translation(0, -1, 0))
        val w = World(
            lights = listOf(light1),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
                Sphere(transform = scaling(0.5, 0.5, 0.5), material = Material(ambient = 1.0)),
                shape
            )
        )
        val r = Ray(point(0, 0, -3), vector(0, -sqrt(2.0) / 2.0, sqrt(2.0) / 2.0))
        val i = Intersection(sqrt(2.0), shape)

        val comps = PreparedComputation(i, r)
        val color = w.shadeHit(comps)

        color.shouldBe(Color(0.87676, 0.92434, 0.82917))
    }

    test("colorAt with mutually reflective surfaces") {
        val w = World(
            lights = listOf(PointLight(point(0, 0, 0), WHITE)),
            objects = listOf(
                Plane(material = Material(reflective = 1.0), transform = translation(0, -1, 0)),
                Plane(material = Material(reflective = 1.0), transform = translation(0, 1, 0)),
            )
        )
        val r = Ray(point(0, 0, 0), vector(0, 1, 0))

        w.colorAt(ray = r).shouldNotBeNull()
    }

    test("The reflected color at maximum recursive depth") {
        val shape = Plane(material = Material(reflective = 0.5), transform = translation(0, -1, 0))
        val w = World(
            lights = listOf(light1),
            objects = listOf(
                Sphere(material = Material(color = Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)),
                Sphere(transform = scaling(0.5, 0.5, 0.5), material = Material(ambient = 1.0)),
                shape
            )
        )
        val r = Ray(point(0, 0, -3), vector(0, -sqrt(2.0) / 2.0, sqrt(2.0) / 2.0))
        val i = Intersection(sqrt(2.0), shape)

        val comps = PreparedComputation(i, r)
        val color = w.reflectedColor(comps, 0)

        color.shouldBe(BLACK)
    }
})