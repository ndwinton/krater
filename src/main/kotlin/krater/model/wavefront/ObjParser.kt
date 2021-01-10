package krater.model.wavefront

import krater.geometry.*
import krater.model.Group
import krater.model.Material
import krater.model.SmoothTriangle
import krater.model.Triangle
import java.io.File

class ObjParser(
    val vertices: List<Tuple>,
    val normals: List<Tuple>,
    val namedGroups: Map<String,Group>,
    ) {
    val defaultGroup: Group = namedGroups[""]!!

    fun toGroup(transform: Matrix = IDENTITY_4X4_MATRIX) = Group(shapes = namedGroups.values.toList(), transform = transform)

    companion object {
        fun fromLines(lines: List<String>, material: Material = Material()): ObjParser {
            val tokenisedLines = tokenise(lines)
            val parsedVertices = parseVertices(tokenisedLines)
            val parsedNormals = parseNormals(tokenisedLines)
            val groupData = splitToGroups(
                currentName = "",
                lines = tokenisedLines.filter { it[0] == "f" || it[0] == "g" },
                seenVertices = parsedVertices,
                seenNormals = parsedNormals,
                material = material)
            return ObjParser(
                vertices = parsedVertices,
                normals = parsedNormals,
                namedGroups = groupData,
            )
        }

        private fun tokenise(lines: List<String>) =
            lines.map(String::trim)
                .filter { !it.isBlank() && !it.startsWith("#") }
                .map { it.split("""\s+""".toRegex()) }

        fun fromFile(file: File, material: Material = Material()): ObjParser {
            val parser = fromLines(file.readLines(), material)
            println("File: $file")
            println("x-axis bounds: ${parser.vertices.minOf { it.x }} -> ${parser.vertices.maxOf { it.x }}")
            println("y-axis bounds: ${parser.vertices.minOf { it.y }} -> ${parser.vertices.maxOf { it.y }}")
            println("z-axis bounds: ${parser.vertices.minOf { it.z }} -> ${parser.vertices.maxOf { it.z }}")
            return parser
        }

        private fun parseVertices(lines: List<List<String>>): List<Tuple> =
            lines.filter { it[0] == "v" }
                .map { parsePoint(it) }

        private fun parsePoint(words: List<String>): Tuple {
            val (x, y, z) = words
                .drop(1)
                .map { num -> num.toDouble() }
            return point(x, y, z)
        }

        private fun parseNormals(lines: List<List<String>>): List<Tuple> =
            lines.filter { it[0] == "vn" }
                .map { parseVector(it) }

        private fun parseVector(words: List<String>): Tuple {
            val (x, y, z) = words
                .drop(1)
                .map { num -> num.toDouble() }
            return vector(x, y, z)
        }

        private fun parseFaces(lines: List<List<String>>, seenVertices: List<Tuple>, seenNormals: List<Tuple>, material: Material): List<Triangle> =
            lines.filter { it[0] == "f" }.flatMap { parseFace(it, seenVertices, seenNormals, material) }

        private fun parseFace(words: List<String>, seenVertices: List<Tuple>, seenNormals: List<Tuple>, material: Material): List<Triangle> {
            val values = words.drop(1)
            return if (values[0].contains("/")) parseSmoothFace(values, seenVertices, seenNormals, material)
            else return parsePlainFace(values, seenVertices, material)
        }

        private fun parsePlainFace(values: List<String>, seenVertices: List<Tuple>, material: Material): List<Triangle> {
            val vertexList = values.map { num -> seenVertices[num.toInt() - 1] }
            val baseVertex = vertexList.first()
            return vertexList.drop(1).zipWithNext { a, b -> Triangle(p1 = baseVertex, p2 = a, p3 = b, material = material) }
        }

        private fun parseSmoothFace(values: List<String>, seenVertices: List<Tuple>, seenNormals: List<Tuple>, material: Material): List<Triangle> {
            val vertexList = values.map { value -> seenVertices[value.split("/")[0].toInt() - 1] }
            val normalList = values.map { value -> seenNormals[value.split("/")[2].toInt() - 1] }
            val baseVertex = vertexList.first()
            val baseNormal = normalList.first()
            val vertexPairs = vertexList.drop(1).zipWithNext { a, b -> Pair(a, b) }
            val normalPairs = normalList.drop(1).zipWithNext { a, b -> Pair(a, b) }

            return vertexPairs.zip(normalPairs) { p, n ->
                SmoothTriangle(
                    p1 = baseVertex, p2 = p.first, p3 = p.second,
                    n1 = baseNormal, n2 = n.first, n3 = n.second,
                    material = material
                )
            }
        }

        private tailrec fun splitToGroups(
            currentName: String,
            lines: List<List<String>>,
            seenVertices: List<Tuple>,
            seenNormals: List<Tuple>,
            result: Map<String,Group> = emptyMap(),
            material: Material
        ): Map<String,Group> {
            val currentFaces = lines.takeWhile { it[0] != "g" }
            val currentGroup = Group(shapes = parseFaces(currentFaces, seenVertices, seenNormals, material))
            val groupEntry = Pair(currentName, currentGroup)

            val remainder = lines.drop(currentFaces.size)
            if (remainder.isEmpty()) return result + groupEntry

            val nextName = remainder.first()[1]
            return splitToGroups(nextName, remainder.drop(1), seenVertices, seenNormals,result + groupEntry, material)
        }
    }
}