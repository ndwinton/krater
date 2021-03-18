package test.model.pattern.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.geometry.near
import krater.geometry.point
import krater.model.pattern.map.UVPoint
import krater.model.pattern.map.planarMap
import krater.model.pattern.map.sphericalMap
import kotlin.math.sqrt

class MappingSpec : FunSpec({

    test("Using a spherical mapping onto a 3D point") {
        table(
            headers("point", "u", "v"),
            row(point(0, 0, -1), 0.0, 0.5),
            row(point(1, 0, 0), 0.25, 0.5),
            row(point(0, 0, 1), 0.5, 0.5),
            row(point(-1, 0, 0), 0.75, 0.5),
            row(point(0, 1, 0), 0.5, 1.0),
            row(point(0, -1, 0), 0.5, 0.0),
            row(point(sqrt(2.0) / 2.0, sqrt(2.0) / 2.0, 0), 0.25, 0.75),
        ).forAll { point, u, v ->
            val uvPoint = sphericalMap(point)
            uvPoint.u.shouldBe(u)
            uvPoint.v.shouldBe(v)
        }
    }

    test("Using a planar mapping onto a 3D point") {
        table(
            headers("point", "u", "v"),
            row(point(0.25, 0, 0.5), 0.25, 0.5),
            row(point(0.25, 0, -0.25), 0.25, -0.25),
            row(point(0.25, 0.5, -0.25), 0.25, -0.25),
            row(point(1.25, 0, 0.5), 0.25, 0.5),
            row(point(0.25, 0, -1.75), 0.25, -0.75),
            row(point(1, 0, -1), 0.0, 0.0),
            row(point(0, 0, 0), 0.0, 0.0),
        ).forAll { point, u, v ->
            val uv = planarMap(point)
            uv.u.near(u).shouldBe(true)
            uv.v.near(v).shouldBe(true)
        }
    }
})


