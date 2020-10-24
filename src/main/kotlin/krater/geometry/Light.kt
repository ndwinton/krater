package krater.geometry

import krater.canvas.Color

interface Light {
    val position: Tuple
    val intensity: Color
}