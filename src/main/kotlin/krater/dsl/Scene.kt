package krater.dsl

import krater.canvas.Color
import krater.canvas.WHITE
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.point
import krater.model.Camera
import krater.model.Light
import krater.model.PointLight
import krater.model.World
import krater.model.shapes.Shape
import java.lang.Math.PI
import java.text.FieldPosition

class Scene() {
    val world: World
        get() = World(lights = lights)
    var camera: Camera = Camera(10, 100, PI / 2)
    var lights: MutableList<Light> = mutableListOf(PointLight(point(-5, 5, -5), WHITE))

    fun camera(init: CameraDsl.() -> Unit): Camera {
        val dsl = CameraDsl()
        dsl.init()
        camera = Camera(hsize = dsl.width, vsize = dsl.height, fieldOfView = dsl.fieldOfView, transform = dsl.transform)
        return camera
    }

    fun lights(init: LightDsl.() -> Unit): MutableList<Light> {
        lights = mutableListOf()
        val dsl = LightDsl(lights = lights)
        dsl.init()
        return lights
    }
}

class CameraDsl(
    var width: Int = 100,
    var height: Int = 100,
    var fieldOfView: Double = PI / 2,
    var transform: Matrix = IDENTITY_4X4_MATRIX
)

class LightDsl(var lights: MutableList<Light> = mutableListOf()) {

    fun pointLight(init: PointLightDsl.() -> Unit): PointLightDsl {
        val dsl = PointLightDsl()
        dsl.init()
        lights.add(PointLight(position = dsl.position, intensity = dsl.color))
        return dsl
    }

    fun add(light: Light) {
        lights.add(light)
    }
}

class PointLightDsl {
    lateinit var position: Tuple
    var color: Color = WHITE
}

fun scene(init: Scene.() -> Unit): Scene {
    val scene = Scene()
    scene.init()
    return scene
}

fun color(hexString: String): Color =
    hexString.dropWhile { it == '#' }
        .chunked(2)
        .map { it.toInt(16) / 255.0 }
        .let { Color(it[0], it[1], it[2]) }

fun color(r: Int, g: Int, b: Int) = Color(r / 255.0, g / 255.0, b / 255.0)

fun color(r: Double, g: Double, b: Double) = Color(r, g, b)