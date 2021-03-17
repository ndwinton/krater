package krater.model.pattern.map

import krater.canvas.Color

interface UVTexture {
    fun uvColorAt(uvPoint: UVPoint): Color
}