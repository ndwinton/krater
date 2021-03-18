package krater.model.pattern.map

import krater.canvas.Color

interface Texture {
    fun colorAtUV(uvPoint: UVPoint): Color
}