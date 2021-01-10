package krater.model

import krater.geometry.*
import kotlin.math.abs

class SmoothTriangle(p1: Tuple, p2: Tuple, p3: Tuple,
                     val n1: Tuple, val n2: Tuple, val n3: Tuple,
                     material: Material = Material(),
                     transform: Matrix = IDENTITY_4X4_MATRIX
) : Triangle(p1, p2, p3, material, transform) {

    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple =
        n2 * intersection.u + n3 * intersection.v + n1 * (1 - intersection.u - intersection.v)
}