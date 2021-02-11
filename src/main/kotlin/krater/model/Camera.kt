package krater.model

import kotlinx.coroutines.*
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
            val row = renderRow(y, world, image)
            row.forEachIndexed { x, color -> image[x, y] = color }
        }
        return image
    }

    private fun renderRow(y: Int, world: World, image: Canvas) = runBlocking {
            (0 until hsize).map { x ->
                async(Dispatchers.Default) {
                    world.colorAt(rayForPixel(x, y))
                }
            }.map { it.await() }
        }
}
