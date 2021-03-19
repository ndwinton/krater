package krater.model.pattern.map

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Tuple
import krater.model.pattern.Pattern
import kotlin.math.abs
import kotlin.math.max

internal enum class CubeFace {
    LEFT, RIGHT, FRONT, BACK, UP, DOWN
}

class CubeMap(
    private val left: Texture,
    private val front: Texture,
    private val right: Texture,
    private val back: Texture,
    private val up: Texture,
    private val down: Texture
) : Pattern(IDENTITY_4X4_MATRIX) {

    companion object {
        internal fun faceFromPoint(point: Tuple): CubeFace {
            val absX = abs(point.x)
            val absY = abs(point.y)
            val absZ = abs(point.z)
            return when (max(max(absX, absY), absZ)) {
                point.x -> CubeFace.RIGHT
                -point.x -> CubeFace.LEFT
                point.y -> CubeFace.UP
                -point.y -> CubeFace.DOWN
                point.z -> CubeFace.FRONT
                else -> CubeFace.BACK
            }
        }

        internal fun uvFront(point: Tuple) =
            UVPoint(((point.x + 1) % 2.0) / 2.0, ((point.y + 1) % 2.0) / 2.0)

        internal fun uvBack(point: Tuple) =
            UVPoint(((1 - point.x) % 2.0) / 2.0, ((point.y + 1) % 2.0) / 2.0)

        internal fun uvLeft(point: Tuple) =
            UVPoint(((point.z + 1) % 2.0) / 2.0, ((point.y + 1) % 2.0) / 2.0)

        internal fun uvRight(point: Tuple) =
            UVPoint(((1 - point.z) % 2.0) / 2.0, ((point.y + 1) % 2.0) / 2.0)

        internal fun uvUp(point: Tuple) =
            UVPoint(((point.x + 1) % 2.0) / 2.0, ((1 - point.z) % 2.0) / 2.0)

        internal fun uvDown(point: Tuple) =
            UVPoint(((point.x + 1) % 2.0) / 2.0, ((point.z + 1) % 2.0) / 2.0)
    }

    override fun colorAt(point: Tuple): Color = when (faceFromPoint(point)) {
        CubeFace.LEFT -> left.colorAtUV(uvLeft(point))
        CubeFace.RIGHT -> right.colorAtUV(uvRight(point))
        CubeFace.FRONT -> front.colorAtUV(uvFront(point))
        CubeFace.BACK -> back.colorAtUV(uvBack(point))
        CubeFace.UP -> up.colorAtUV(uvUp(point))
        CubeFace.DOWN -> down.colorAtUV(uvDown(point))
    }
}