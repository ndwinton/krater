package krater.model.pattern

import krater.canvas.Color
import krater.model.pattern.map.UVPoint

interface Texture {
    fun colorAtUV(uvPoint: UVPoint): Color
}