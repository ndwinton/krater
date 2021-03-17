package krater.model.pattern.map

import krater.canvas.Color
import krater.geometry.near
import kotlin.math.floor

class UVChecker(val width: Int, val height: Int, val a: Color, val b: Color) : UVTexture {
    override fun uvColorAt(uvPoint: UVPoint): Color =
        if (((floor(uvPoint.u * width) + floor(uvPoint.v * height)) % 2.0).near(0.0)) a else b
}