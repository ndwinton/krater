package krater.canvas

class Canvas(val width: Int, val height: Int) {

    private val pixels = Array(height) { Array(width) { Color(0.0, 0.0, 0.0 )} }

    operator fun get(x: Int, y: Int) = pixels[y][x]
    operator fun set(x: Int, y: Int, value: Color) {
        pixels[y][x] = value
    }

    fun toPPM(): String {
        return """
            PPM
            5 3
            255
        """.trimIndent() + "\n" + pixels.joinToString("\n") { row ->
            row.flatMap { pixel ->
                pixel.toScaledTriple(255).toList()
            }.chunked(70 / 4) // Ensure lines stay under 70 chars - 3-digits + space in group
            .joinToString("\n") {
                it.joinToString(" ")
            }
        } + "\n"
    }

}