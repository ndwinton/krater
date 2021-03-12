package test.model.shapes

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import krater.geometry.point
import krater.geometry.translation
import krater.geometry.vector
import krater.model.*
import krater.model.shapes.*

class CsgSpec : FunSpec({
    test("CSG is created with an operation and two shapes") {
        val s1 = Sphere()
        val s2 = Cube()

        val c = Csg(operation = UNION, left = s1, right = s2)

        c.left.shouldBe(s1)
        c.right.shouldBe(s2)
        s1.parent.shouldBe(c)
        s2.parent.shouldBe(c)
    }

    test("Evaluating the intersection allowed rule for a union operation") {
        table(
            headers("leftHit", "inLeft", "inRight", "result"),
            row(true, true, true, false),
            row(true, true, false, true),
            row(true, false, true, false),
            row(true, false, false, true),
            row(false, true, true, false),
            row(false, true, false, false),
            row(false, false, true, true),
            row(false, false, false, true),
        ).forAll {
            leftHit, inLeft, inRight, result -> UNION(leftHit, inLeft, inRight).shouldBe(result)
        }
    }

    test("Evaluating the intersection allowed rule for an intersect operation") {
        table(
            headers("leftHit", "inLeft", "inRight", "result"),
            row(true, true, true, true),
            row(true, true, false, false),
            row(true, false, true, true),
            row(true, false, false, false),
            row(false, true, true, true),
            row(false, true, false, true),
            row(false, false, true, false),
            row(false, false, false, false),
        ).forAll {
                leftHit, inLeft, inRight, result -> INTERSECT(leftHit, inLeft, inRight).shouldBe(result)
        }
    }

    test("Evaluating the intersection allowed rule for a difference operation") {
        table(
            headers("leftHit", "inLeft", "inRight", "result"),
            row(true, true, true, false),
            row(true, true, false, true),
            row(true, false, true, false),
            row(true, false, false, true),
            row(false, true, true, true),
            row(false, true, false, true),
            row(false, false, true, false),
            row(false, false, false, false),
        ).forAll {
                leftHit, inLeft, inRight, result -> DIFFERENCE(leftHit, inLeft, inRight).shouldBe(result)
        }
    }

    test("Filtering a list of intersections") {
        table(
            headers("op", "x0", "x1"),
            row(UNION, 0, 3),
            row(INTERSECT, 1, 2),
            row(DIFFERENCE, 0, 1),
        ).forAll { op, x0, x1 ->
            val s1 = Sphere()
            val s2 = Cube()
            val xs = listOf(Intersection(1.0, s1), Intersection(2.0, s2), Intersection(3.0, s1), Intersection(4.0, s2))

            val c = Csg(op, s1, s2)

            val result = c.filterIntersections(xs)

            result.size.shouldBe(2)
            result[0].shouldBe(xs[x0])
            result[1].shouldBe(xs[x1])
        }
    }

    test("A ray misses a CSG object") {
        val c = Csg(UNION, Sphere(), Cube())
        val r = Ray(point(0, 2, -5), vector(0, 0, 1))

        val xs = c.localIntersect(r)

        xs.shouldBeEmpty()
    }


    test("A ray hits a CSG object") {
        val s1 = Sphere()
        val s2 = Sphere(transform = translation(0, 0, 0.5))
        val c = Csg(UNION, s1, s2)
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))

        val xs = c.localIntersect(r)

        xs.size.shouldBe(2)
        xs[0].t.shouldBe(4.0)
        xs[0].shape.shouldBe(s1)
        xs[1].shape.shouldBe(s2)
        xs[1].t.shouldBe(6.5)
    }

    test("A CSG shape has a bounding box that contains its children") {
        val left = Sphere()
        val right = Sphere(transform = translation(2, 3, 4))
        val shape = Csg(operation = DIFFERENCE, left, right)
        shape.boundingBox.min.shouldBe(point(-1, -1, -1))
        shape.boundingBox.max.shouldBe(point(3, 4, 5))
    }

    test("Intersecting ray+csg doesn't test children if box is missed") {
        val left = TestShape()
        val right = TestShape()
        val shape = Csg(DIFFERENCE, left, right)
        val r = Ray(point(0, 0, -5), vector(0, 1, 0))
        val xs = shape.intersect(r)
        left.savedRay.shouldBeNull()
        right.savedRay.shouldBeNull()
    }

    test("Intersecting ray+csg tests children if box is hit") {
        val left = TestShape()
        val right = TestShape()
        val shape = Csg(DIFFERENCE, left, right)
        val r = Ray(point(0, 0, -5), vector(0, 0, 1))
        val xs = shape.intersect(r)
        left.savedRay.shouldNotBeNull()
        right.savedRay.shouldNotBeNull()
    }

    test("Subdividing a GSG shape subdivides its children") {
        val s1 = Sphere(transform = translation(-1.5, 0, 0))
        val s2 = Sphere(transform = translation(1.5, 0, 0))
        val left = Group(shapes = listOf(s1, s2))
        val s3 = Sphere(transform = translation(0, 0, -1.5))
        val s4 = Sphere(transform = translation( 0, 0, 1.5))
        val right = Group(shapes = listOf(s3, s4))
        val shape = Csg(DIFFERENCE, left, right)
        val divided = shape.divide(1) as Csg
        val l = divided.left as Group
        l.shapes.forEach { it.shouldBeInstanceOf<Group>() }
        val r = divided.right as Group
        r.shapes.forEach { it.shouldBeInstanceOf<Group>() }
    }
})