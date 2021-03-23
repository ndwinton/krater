package krater.model.pattern

import krater.canvas.Color
import krater.model.pattern.map.UVPoint
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.round

class ImageTexture(file: File) : Texture {
    private val image = ImageIO.read(file)
    private val height = image.height
    private val width = image.width

    override fun colorAtUV(uvPoint: UVPoint): Color {
        val uPixel = round(uvPoint.u * (width - 1)).toInt()
        val vPixel = round((1.0 - uvPoint.v) * (height - 1)).toInt()
        val rgb = image.getRGB(uPixel, vPixel) and 0xffffff
        return Color(
            ((rgb shr 16) and 0xff) / 255.0,
            ((rgb shr 8) and 0xff) / 255.0,
                (rgb and 0xff) / 255.0)
    }
}