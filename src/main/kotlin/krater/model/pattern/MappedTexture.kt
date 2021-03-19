package krater.model.pattern

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.model.pattern.map.Mapping

class MappedTexture(val texture: Texture, val mapping: Mapping, transform: Matrix = IDENTITY_4X4_MATRIX) :
    Pattern(transform) {

    override fun colorAt(point: Tuple): Color = texture.colorAtUV(mapping.map(point))
}
