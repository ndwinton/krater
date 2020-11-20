package krater.model

import krater.geometry.*

class Plane(
    transform: Matrix = IDENTITY_4X4_MATRIX,
    material: Material = Material()
) : Shape(transform = transform, material = material) {

    override fun localNormalAt(objectPoint: Tuple): Tuple = vector(0, 1, 0)

    override fun localIntersect(objectRay: Ray): List<Intersection> =
        if (objectRay.direction.y.near(0.0)) emptyList()
        else listOf(Intersection(-objectRay.origin.y / objectRay.direction.y, this))
}