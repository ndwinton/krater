package krater.model.pattern.map

import krater.canvas.Color
import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.model.pattern.Pattern

class TextureMap(val texture: Texture, val mapping: Mapping, transform: Matrix = IDENTITY_4X4_MATRIX) :
    Pattern(transform) {

    override fun colorAt(point: Tuple): Color = texture.colorAtUV(mapping.map(point))
}
