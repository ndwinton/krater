package krater

import krater.canvas.Canvas
import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.point
import java.io.File
import kotlin.math.PI


fun main(args:Array<String>) {

    val canvas = Canvas(100, 100)
    val origin = point(0, 0, 0)

    (0..11).forEach {
        val dot = IDENTITY_4X4_MATRIX.translate(40, 0, 0).rotateZ(it * PI / 6) * origin
        canvas[dot.x.toInt() + 50, dot.y.toInt() + 50] = Color(1.0, 1.0, 1.0)
    }
    File("clock.ppm").writeText(canvas.toPPM())
}
