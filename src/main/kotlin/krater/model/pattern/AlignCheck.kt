package krater.model.pattern

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Tuple
import krater.model.pattern.Pattern

class AlignCheck(val main: Color, val ul: Color, val ur: Color, val bl: Color, val br: Color) : Pattern(transform = IDENTITY_4X4_MATRIX) {
    override fun colorAt(point: Tuple): Color {
        if (point.z > 0.8 && point.x < 0.2) return ul
        if (point.z > 0.8 && point.x > 0.8) return ur
        if (point.z < 0.2 && point.x < 0.2) return bl
        if (point.z < 0.2 && point.x > 0.8) return br
        return main
    }
}
