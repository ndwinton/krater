package krater.model

import krater.canvas.Color
import krater.geometry.Tuple

interface Light {
    val position: Tuple
    val intensity: Color
}