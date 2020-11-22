package krater.canvas

import krater.geometry.Tuple

interface ColorProvider {
    fun colorAtObject(objectPoint: Tuple): Color
}