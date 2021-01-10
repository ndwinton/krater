package test.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import krater.geometry.near
import krater.geometry.point
import krater.geometry.vector
import krater.model.*

class SmoothTriangleSpec :  FunSpec({

    val p1 = point(0, 1, 0)
    val p2 = point(-1, 0, 0)
    val p3 = point(1, 0, 0)
    val n1 = vector(0, 1, 0)
    val n2 = vector(-1, 0, 0)
    val n3 = vector(1, 0, 0)
    val tri = SmoothTriangle(p1, p2, p3, n1, n2, n3)

    test("Constructing a smooth triangle") {

        tri.p1.shouldBe(p1)
        tri.p2.shouldBe(p2)
        tri.p3.shouldBe(p3)
        tri.n1.shouldBe(n1)
        tri.n2.shouldBe(n2)
        tri.n3.shouldBe(n3)
    }


    test("An intersection with a smooth triangle stores u/v") {
        val r = Ray(point(-0.2, 0.3, -2), vector(0, 0, 1))

        val xs = tri.localIntersect(r)

        xs[0].u.near(0.45).shouldBe(true)
        xs[0].v.near(0.25).shouldBe(true)
    }

    test("A smooth triangle uses u/v to interpolate the normal") {
        val i = Intersection(1.0, tri, u = 0.45, v = 0.25)

        val n = tri.normalAt(point(0, 0, 0), i)

        n.shouldBe(vector(-0.5547, 0.83205, 0.0))
    }

    test("Preparing the normal on a smooth triangle") {
        val i = Intersection(1.0, tri, 0.45, 0.25)
        val r = Ray(point(-0.2, 0.3, -2), vector(0, 0, 1))
        val xs = listOf(i)

        val comps = PreparedComputation(i, r, xs)

        comps.normalv.shouldBe(vector(-0.5547, 0.83205, 0))
    }
})