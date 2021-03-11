package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import krater.geometry.near
import krater.geometry.nearHash

class DoubleExtensionsSpec : FunSpec({
    test("Comparing near double values") {

        1.000005.near(1.0).shouldBe(true)
        1.000011.near(1.0).shouldBe(false)
        0.999995.near(1.0).shouldBe(true)

        1.999995.near(2.0).shouldBe(true)
        (-3.000009).near(-3.0).shouldBe(true)
        4.499999.near(4.5).shouldBe(true)
    }

    test("Near doubles should hash to same value") {
        // This is tricky - things that will be considered equal must hash
        // to the same value. Things that are not equal *may* hash to the
        // same value. And we want things that are obviously different to
        // hash differently.

        // These are within tolerance
        1.0.nearHash().shouldBe(1.000009.nearHash())
        1.0.nearHash().shouldBe(0.999999.nearHash())

        // Definitely outside tolerance (some values closer may still hash the same)
        1.0.nearHash().shouldNotBe(1.0001.nearHash())
        1.0.nearHash().shouldNotBe(1.00006.nearHash())
        1.0.nearHash().shouldNotBe(0.0009.nearHash())
        1.0.nearHash().shouldNotBe(0.99994.nearHash())

        // Within tolerance
        (-1.0).nearHash().shouldBe((-1.000009).nearHash())
        (-1.0).nearHash().shouldBe((-0.999999).nearHash())

        // Outside tolerance
        (-1.0).nearHash().shouldNotBe((-1.0001).nearHash())
        (-1.0).nearHash().shouldNotBe((-1.00006).nearHash())
        (-1.0).nearHash().shouldNotBe((-0.0009).nearHash())
        (-1.0).nearHash().shouldNotBe((-0.99994).nearHash())
    }

    test("Infinities should be handled") {
        Double.POSITIVE_INFINITY.near(Double.POSITIVE_INFINITY).shouldBe(true)
        Double.NEGATIVE_INFINITY.near(Double.NEGATIVE_INFINITY).shouldBe(true)
    }
})