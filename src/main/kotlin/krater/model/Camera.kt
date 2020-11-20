package krater.model

import krater.canvas.Canvas
import krater.geometry.*
import kotlin.math.tan

class Camera(val hsize: Int, val vsize: Int, val fieldOfView: Double, val transform: Matrix  = IDENTITY_4X4_MATRIX) {
    private val halfView = tan(fieldOfView / 2)
    private val aspect = (hsize.toDouble()) / (vsize.toDouble())
    private val halfWidth = if (aspect >= 1) halfView else halfView * aspect
    private val halfHeight = if (aspect >= 1) halfView / aspect else halfView
    val pixelSize = (halfWidth * 2) / hsize

    fun rayForPixel(px: Int, py: Int): Ray {
        val offsetX = (px + 0.5) * pixelSize
        val offsetY = (py + 0.5) * pixelSize
        val worldX = halfWidth - offsetX
        val worldY = halfHeight - offsetY
        val inverseTransform = transform.inverse()
        val pixel = inverseTransform * point(worldX, worldY, -1)
        val origin = inverseTransform * point(0, 0, 0)
        val direction = (pixel - origin).normalize()
        return Ray(origin, direction)
    }

    fun render(world: World): Canvas {
        val image = Canvas(hsize, vsize)
        (0 until vsize).forEach { y ->
            (0 until hsize).forEach { x ->
                val ray = rayForPixel(x, y)
                val color = world.colorAt(ray)
                image[x, y] = color
            }
        }
        return image
    }
}