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
})