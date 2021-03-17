package test.model.pattern.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import krater.geometry.point
import krater.model.pattern.map.sphericalMap
import kotlin.math.sqrt

class MappingSpec : FunSpec({
    table(
        headers("point", "u", "v"),
        row(point(0, 0, -1), 0.0, 0.5),
        row(point(1, 0, 0), 0.25, 0.5),
        row(point(0, 0, 1), 0.5, 0.5),
        row(point(-1, 0, 0), 0.75, 0.5),
        row(point(0, 1, 0), 0.5, 1.0),
        row(point(0, -1, 0), 0.5, 0.0),
        row(point(sqrt(2.0)/2.0, sqrt(2.0)/2.0, 0), 0.25, 0.75),
    ).forAll { point, u, v ->
        val uvPoint = sphericalMap(point)
        uvPoint.u.shouldBe(u)
        uvPoint.v.shouldBe(v)
    }
})

