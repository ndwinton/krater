package test.model.shapes

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.geometry.point
import krater.geometry.rotationX
import krater.geometry.rotationY
import krater.geometry.vector
import krater.model.Ray
import krater.model.shapes.BoundingBox
import kotlin.math.PI

class BoundingBoxSpec : FunSpec ({

    test("Creating an empty bounding box") {
        val box = BoundingBox()
        box.min.shouldBe(point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY))
        box.max.shouldBe(point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY))
    }

    test("Creating a bounding box with volume") {
        val box = BoundingBox(min = point(-1, -2, -3), max = point(3, 2, 1))
        box.min.shouldBe(point(-1, -2, -3))
        box.max.shouldBe(point(3, 2, 1))
    }

    test("Adding points to an empty bounding box") {
        val box = BoundingBox()
        val added = box + point(-5, 2, 0) + point(7, 0, -3)
        added.min.shouldBe(point(-5, 0, -3))
        added.max.shouldBe(point(7, 2, 0))
    }

    test("Adding one bounding box to another") {
        val box1 = BoundingBox(min = point(-5, -2, 0), max = point(7, 4, 4))
        val box2 = BoundingBox(min = point(8, -7, -2), max = point(14, 2, 8))
        val added = box1 + box2
        added.min.shouldBe(point(-5, -7, -2))
        added.max.shouldBe(point(14, 4, 8))
    }

    test("Checking to see if a box contains a given point") {
        val box = BoundingBox(min = point(5, -2, 0), max = point(11,4, 7))

        table(
            headers("point", "result"),
            row(point(5, -2, 0), true),
            row(point(11, 4, 7), true),
            row(point(8, 1, 3), true),
            row(point(3, 0, 3), false),
            row(point(8, -4, 3), false),
            row(point(8, 1, -1), false),
            row(point(13, 1, 3), false),
            row(point(8, 5, 3), false),
            row(point(8, 1, 8), false)
        ).forAll { point, result -> box.contains(point).shouldBe(result) }
    }

    test("Checking to see if a box contains a given box") {
        val box = BoundingBox(min = point(5, -2, 0), max = point(11,4, 7))

        table(
            headers("min", "max", "result"),
            row(point(5, -2, 0), point(11, 4, 7), true),
            row(point(6, -1, 1), point(10, 3, 6), true),
            row(point(4, -3, -1), point(10, 3, 6), false),
            row(point(6, -1, 1), point(12, 5, 8), false),
        ).forAll { min, max, result -> box.contains(BoundingBox(min = min, max = max)).shouldBe(result) }
    }

    test("Transforming a bounding box") {
        val box = BoundingBox(min = point(-1, -1, -1), max = point(1, 1, 1))
        val matrix = rotationX(PI / 4) * rotationY(PI / 4)

        val box2 = box.transform(matrix)

        box2.min.shouldBe(point(-1.41421, -1.7071, -1.7071))
        box2.max.shouldBe(point(1.41421, 1.7071, 1.7071))
    }

    test("Intersecting a ray with a bounding box at the origin") {
        val box = BoundingBox(min = point(-1, -1, -1), max = point(1, 1, 1))
        table(
            headers("origin", "direction", "result"),
            row(point(5, 0.5, 0), vector(-1, 0, 0), true),
            row(point(-5, 0.5, 0), vector(1, 0, 0), true),
            row(point(0.5, 5, 0), vector(0, -1, 0), true),
            row(point(0.5, -5, 0), vector(0, 1, 0), true),
            row(point(0.5, 0, 5), vector(0, 0, -1), true),
            row(point(0.5, 0, -5), vector(0, 0, 1), true),
            row(point(0, 0.5, 0), vector(0, 0, 1), true),
            row(point(-2, 0, 0), vector(2, 4, 6), false),
            row(point(0, -2, 0), vector(6, 2, 4), false),
            row(point(0, 0, -2), vector(4, 6, 2), false),
            row(point(2, 0, 2), vector(0, 0, -1), false),
            row(point(0, 2, 2), vector(0, -1, 0), false),
            row(point(0, 1, 2), vector(-1, 0, 0), false),
        ).forAll { origin, direction, result ->
            val r = Ray(origin, direction)
            box.isIntersectedBy(r).shouldBe(result)
        }
    }
    
    test("Intersecting a ray with a non-cubic bounding box") {
        val box = BoundingBox(min = point(5, -2, 0), max = point(11, 4, 7))
        table(
            headers("origin", "direction", "result"),
            row(point(15, 1, 2), vector(-1, 0, 0), true),
            row(point(-5, -1, 4), vector(1, 0, 0), true),
            row(point(7, 6, 5), vector(0, -1, 0), true),
            row(point(9, -5, 6), vector(0, 1, 0), true),
            row(point(8, 2, 12), vector(0, 0, -1), true),
            row(point(6, 0, -5), vector(0, 0, 1), true),
            row(point(8, 1, 3.5), vector(0, 0, 1), true),
            row(point(9, -1, -8), vector(2, 4, 6), false),
            row(point(8, 3, -4), vector(6, 2, 4), false),
            row(point(9, -1, -2), vector(4, 6, 2), false),
            row(point(4, 0, 9), vector(0, 0, -1), false),
            row(point(8, 6, -1), vector(0, -1, 0), false),
            row(point(12, 5, 4), vector(-1, 0, 0), false),
        ).forAll { origin, direction, result ->
            val normalised = direction.normalize()
            val r = Ray(origin, normalised)
            box.isIntersectedBy(r).shouldBe(result)
        }
    }
})