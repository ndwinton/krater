package krater.model.wavefront

import krater.geometry.IDENTITY_4X4_MATRIX
import krater.geometry.Matrix
import krater.geometry.Tuple
import krater.geometry.point
import krater.model.Group
import krater.model.Material
import krater.model.Triangle
import java.io.File

class ObjParser(
    val vertices: List<Tuple>,
    val namedGroups: Map<String,Group>,
    ) {
    val defaultGroup: Group = namedGroups[""]!!

    fun toGroup(transform: Matrix = IDENTITY_4X4_MATRIX) = Group(shapes = namedGroups.values.toList(), transform = transform)

    companion object {
        fun fromLines(lines: List<String>, material: Material = Material()): ObjParser {
            val parsedVertices = parseVertices(lines)
            val groupData = splitToGroups("", lines.filter { it.startsWith("f") || it.startsWith("g") }, parsedVertices, material = material)
            return ObjParser(
                vertices = parsedVertices,
                namedGroups = groupData,
            )
        }

        fun fromFile(file: File, material: Material = Material()) = fromLines(file.readLines(), material)

        private fun parseVertices(lines: List<String>): List<Tuple> =
            lines.filter { it.startsWith("v") }
                .map { parseVertex(it) }

        private fun parseVertex(line: String): Tuple {
            val (x, y, z) = line.split(" ")
                .drop(1)
                .map { num -> num.toDouble() }
            return point(x, y, z)
        }

        private fun parseFaces(lines: List<String>, seenVertices: List<Tuple>, material: Material): List<Triangle> =
            lines.filter { it.startsWith("f") }.flatMap { parseFace(it, seenVertices, material) }

        private fun parseFace(line: String, seenVertices: List<Tuple>, material: Material): List<Triangle> {
            val vertexList = line.split(" ")
                .drop(1)
                .map { num -> seenVertices[num.toInt() - 1] }
            val baseVertex = vertexList.first()
            return vertexList.drop(1).zipWithNext { a, b -> Triangle(p1 = baseVertex, p2 = a, p3 = b, material = material) }
        }

        private tailrec fun splitToGroups(
            currentName: String,
            lines: List<String>,
            parsedVertices: List<Tuple>,
            result: Map<String,Group> = emptyMap(),
            material: Material
        ): Map<String,Group> {
            val currentFaces = lines.takeWhile { !it.startsWith("g") }
            val currentGroup = Group(shapes = parseFaces(currentFaces, parsedVertices, material))
            val groupEntry = Pair(currentName, currentGroup)

            val remainder = lines.drop(currentFaces.size)
            if (remainder.isEmpty()) return result + groupEntry

            val nextName = remainder.first().split(" ")[1]
            return splitToGroups(nextName, remainder.drop(1), parsedVertices, result + groupEntry, material)
        }
    }
}