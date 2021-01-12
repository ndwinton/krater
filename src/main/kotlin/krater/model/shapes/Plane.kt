package krater.model.shapes

import krater.geometry.*
import krater.model.Intersection
import krater.model.Material
import krater.model.Ray

class Plane(
    transform: Matrix = IDENTITY_4X4_MATRIX,
    material: Material = Material()
) : Shape(transform = transform, material = material) {

    override fun localNormalAt(objectPoint: Tuple, intersection: Intersection): Tuple = vector(0, 1, 0)

    override fun localIntersect(objectRay: Ray): List<Intersection> =
        if (objectRay.direction.y.near(0.0)) emptyList()
        else listOf(Intersection(-objectRay.origin.y / objectRay.direction.y, this))
}