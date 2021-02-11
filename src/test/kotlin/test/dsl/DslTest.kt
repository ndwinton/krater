package test.dsl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import krater.canvas.WHITE
import krater.dsl.color
import krater.dsl.scene
import krater.geometry.point
import krater.geometry.scaling
import krater.model.Camera
import krater.model.PointLight
import krater.model.World
import krater.model.shapes.Cube
import krater.model.shapes.Sphere
import java.lang.Math.PI

class DslTest : FunSpec({

    test("Should be able to define a scene and retrieve the default world") {
        val scene = scene { }

        scene.world.shouldBeInstanceOf<World>()
    }

    test("Should be able to create point-lights in the scene") {
        val scene = scene {
            lights {
                pointLight {
                    position = point(1, 2, 3)
                    color = WHITE
                }

                pointLight {
                    position = point(4, 5, 6)
                    color = color("#ff0000")
                }
            }
        }

        scene.world.lights.size.shouldBe(2)
        scene.world.lights[0].position.shouldBe(point(1, 2, 3))
    }

    test("Should be able to add manually created point-lights to the scene") {
        val scene = scene {
            lights {
                pointLight {
                    position = point(1, 2, 3)
                    color = WHITE
                }

                add(PointLight(point(4, 5, 6), WHITE))
            }
        }

        scene.world.lights.size.shouldBe(2)
        scene.world.lights[1].position.shouldBe(point(4, 5, 6))
    }

    test("Should be able to define a camera in a scene") {
        val scene = scene {
            camera {
                width = 123
                height = 456
                fieldOfView = PI
                transform = scaling(1, 0, 1)
            }
        }

        scene.camera.hsize.shouldBe(123)
        scene.camera.vsize.shouldBe(456)
        scene.camera.fieldOfView.shouldBe(PI)
        scene.camera.transform.shouldBe(scaling(1, 0, 1))
    }

    test("Should be able to define ") {

    }
})