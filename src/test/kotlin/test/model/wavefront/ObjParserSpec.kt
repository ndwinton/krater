package test.model.wavefront

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import krater.geometry.point
import krater.geometry.scaling
import krater.model.Triangle

import krater.model.wavefront.ObjParser

class ObjParserSpec : FunSpec({

    test("Ignoring unrecognised lines") {

        val gibberish = """
            There was a young cleric from Salisbury
            Whose manners were quite Halisbury-Scalisbury
            He ran about Hampshire
            Without any Pampshire
            'Til the bishop compelled him to Walisbury
        """.trimIndent().split("\n")

        val parser = ObjParser.fromLines(gibberish)

        parser.vertices.size.shouldBe(0)
    }

    test("Vertex records") {
        val lines = """
            v -1 1 0
            v -1.0000 0.5000 0.0000
            v 1 0 0
            v 1 1 0
        """.trimIndent().split("\n")

        val parser = ObjParser.fromLines(lines)
        parser.vertices[0].shouldBe(point(-1, 1, 0))
        parser.vertices[1].shouldBe(point(-1, 0.5, 0))
        parser.vertices[2].shouldBe(point(1, 0, 0))
        parser.vertices[3].shouldBe(point(1, 1, 0))
    }

    test("Parsing triangle faces") {
        val lines = """
            v -1 1 0
            v -1 0 0
            v 1 0 0
            v 1 1 0
            
            f 1 2 3
            f 1 3 4
        """.trimIndent().split("\n")

        val parser = ObjParser.fromLines(lines)
        val g = parser.defaultGroup
        val t1 = g.shapes[0] as Triangle
        val t2 = g.shapes[1] as Triangle

        t1.p1.shouldBe(parser.vertices[0])
        t1.p2.shouldBe(parser.vertices[1])
        t1.p3.shouldBe(parser.vertices[2])
        t2.p1.shouldBe(parser.vertices[0])
        t2.p2.shouldBe(parser.vertices[2])
        t2.p3.shouldBe(parser.vertices[3])
    }

    test("Triangulating polygons") {
        val lines ="""
            v -1 1 0
            v -1 0 0
            v 1 0 0
            v 1 1 0
            v 0 2 0
            f 1 2 3 4 5
        """.trimIndent().split("\n")

        val parser = ObjParser.fromLines(lines)
        val g = parser.defaultGroup
        val t1 = g.shapes[0] as Triangle
        val t2 = g.shapes[1] as Triangle
        val t3 = g.shapes[2] as Triangle

        t1.p1.shouldBe(parser.vertices[0])
        t1.p2.shouldBe(parser.vertices[1])
        t1.p3.shouldBe(parser.vertices[2])
        t2.p1.shouldBe(parser.vertices[0])
        t2.p2.shouldBe(parser.vertices[2])
        t2.p3.shouldBe(parser.vertices[3])
        t3.p1.shouldBe(parser.vertices[0])
        t3.p2.shouldBe(parser.vertices[3])
        t3.p3.shouldBe(parser.vertices[4])
    }

    val triangles = """
        v -1 1 0
        v -1 0 0
        v 1 0 0
        v 1 1 0
        
        g FirstGroup
        f 1 2 3
        
        g SecondGroup
        f 1 3 4
    """.trimIndent().split("\n")

    test("Triangles in groups") {
        val parser = ObjParser.fromLines(triangles)

        val g1 = parser.namedGroups["FirstGroup"]!!
        val g2 = parser.namedGroups["SecondGroup"]!!
        val t1 = g1.shapes[0] as Triangle
        val t2 = g2.shapes[0] as Triangle

        t1.p1.shouldBe(parser.vertices[0])
        t1.p2.shouldBe(parser.vertices[1])
        t1.p3.shouldBe(parser.vertices[2])
        t2.p1.shouldBe(parser.vertices[0])
        t2.p2.shouldBe(parser.vertices[2])
        t2.p3.shouldBe(parser.vertices[3])
    }

    test("Converting an OBJ file to a group") {
        val parser = ObjParser.fromLines(triangles)

        val g = parser.toGroup(scaling(2, 2, 2))

        g.shapes.shouldContain(parser.namedGroups["FirstGroup"])
        g.shapes.shouldContain(parser.namedGroups["SecondGroup"])
        g.transform.shouldBe(scaling(2, 2, 2))
    }
})