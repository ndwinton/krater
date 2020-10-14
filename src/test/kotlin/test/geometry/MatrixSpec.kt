package test.geometry

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import krater.geometry.*

class MatrixSpec  : FunSpec({

    test("Constructing and inspecting a 4x4 matrix") {

        val m = Matrix(
            Row(1.0, 2.0, 3.0, 4.0),
            Row(5.5, 6.5, 7.5, 8.5),
            Row(9.0, 10.0, 11.0, 12.0),
            Row(13.5, 145.5, 15.5, 16.5)
        )

        m[0, 0].shouldBe(1.0)
        m[0, 3].shouldBe(4.0)
        m[1, 0].shouldBe(5.5)
        m[1, 2].shouldBe(7.5)
        m[2, 2].shouldBe(11.0)
        m[3, 0].shouldBe(13.5)
        m[3, 2].shouldBe(15.5)
    }

    test("A 2x2 matrix should be representable") {
        val m = Matrix(
            Row(-3, 5),
            Row(1, -2)
        )

        m[0, 0].shouldBe(-3.0)
        m[0, 1].shouldBe(5.0)
        m[1, 0].shouldBe(1.0)
        m[1, 1].shouldBe(-2.0)
    }

    test("A 3x3 matrix should be representable") {
        val m = Matrix(
            Row(-3, 5, 0),
            Row(1, -2, -7),
            Row(0, 1, 1)
        )

        m[0, 0].shouldBe(-3.0)
        m[1, 1].shouldBe(-2.0)
        m[2, 2].shouldBe(1.0)
    }

    test("Matrix equality with identical matrices (within tolerance)") {
        val m1 = Matrix(
            Row(1, 2, 3, 4),
            Row(5, 6, 7, 8),
            Row(9, 8, 7, 6),
            Row(5, 4, 3, 2)
        )
        val m2 = Matrix(
            Row(1, 2, 3, 4),
            Row(5, 6, 7, 8),
            Row(9.000001, 7.999999, 7.000009, 5.999995),
            Row(5, 4, 3, 2)
        )

        (m1 == m2).shouldBe(true)
    }

    test("Matrix equality with different matrices") {
        val m1 = Matrix(
            Row(1, 2, 3, 4),
            Row(5, 6, 7, 8),
            Row(9, 8, 7, 6),
            Row(5, 4, 3, 2)
        )
        val m2 = Matrix(
            Row(5, 4, 3, 2),
            Row(1, 2, 3, 4),
            Row(5, 6, 7, 8),
            Row(9, 8, 7, 6),
        )

        (m1 == m2).shouldBe(false)
    }

    test("Multiplying two matrices") {
        val a = Matrix(
            Row(1, 2, 3, 4),
            Row(5, 6, 7, 8),
            Row(9, 8, 7, 6),
            Row(5, 4, 3, 2)
        )
        val b = Matrix(
            Row(-2, 1, 2, 3),
            Row(3, 2, 1, -1),
            Row(4, 3, 6, 5),
            Row(1, 2, 7, 8)
        )

        (a * b).shouldBe(Matrix(
            Row(20, 22, 50, 48),
            Row(44, 54, 114, 108),
            Row(40, 58, 110, 102),
            Row(16, 26, 46, 42)
        ))
    }

    test("A matrix multiplied by a tuple") {
        val m = Matrix(
            Row(1, 2, 3, 4),
            Row(2, 4, 4, 2),
            Row(8, 6, 4, 1),
            Row(0, 0, 0, 1)
        )
        val t = Tuple(1.0, 2.0, 3.0, 1.0)

        (m * t).shouldBe(Tuple(18.0, 24.0, 33.0, 1.0))
    }

    test("Multiplying a matrix by the identity matrix") {
        val m = Matrix(
            Row(0, 1, 2, 4),
            Row(1, 2, 4, 8),
            Row(2, 4, 8, 16),
            Row(4, 8, 16, 32)
        )

        (m * IDENTITY_4X4_MATRIX).shouldBe(m)
    }

    test("Transposing a matrix") {
        val m = Matrix(
            Row(0, 9, 3, 0),
            Row(9, 8, 0, 8),
            Row(1, 8, 5, 3),
            Row(0, 0, 5, 8)
        )

        m.transpose().shouldBe(Matrix(
            Row(0, 9, 1, 0),
            Row(9, 8, 8, 0),
            Row(3, 0, 5, 5),
            Row(0, 8, 3, 8)
        ))
    }

    test("Transposing the identity matrix") {
        IDENTITY_4X4_MATRIX.transpose().shouldBe(IDENTITY_4X4_MATRIX)
    }

    test("Calculating the determinant of a 2x2 matrix") {
        val a = Matrix(
            Row(1, 5),
            Row(-3, 2)
        )

        a.determinant().shouldBe(17.0)
    }

    test("A submatrix of a 3x3 matrix is a 2x2 matrix") {
        val a = Matrix(
            Row(1, 5, 0),
            Row(-3, 2, 7),
            Row(0, 6, -3)
        )

        a.subMatrix(0, 2).shouldBe(Matrix(
            Row(-3, 2),
            Row(0, 6)
        ))
    }

    test("A submatrix of a 4x4 matrix is a 3x3 matrix") {
        val a = Matrix(
            Row(-6, 1, 1, 6),
            Row(-8, 5, 8, 6),
            Row(-1, 0, 8, 2),
            Row(-7, 1, -1, 1)
        )

        a.subMatrix(2, 1).shouldBe(Matrix(
            Row(-6, 1, 6),
            Row(-8, 8, 6),
            Row(-7, -1, 1)
        ))
    }

    test("Calculating a minor of a 3x3 matrix") {
        val a = Matrix(
            Row(3, 5, 0),
            Row(2, -1, -7),
            Row(6, -1, 5)
        )
        val b = a.subMatrix(1, 0)
        b.determinant().shouldBe(25.0)
        a.minor(1, 0).shouldBe(25.0)
    }

    test("Calculating the cofactor of a 3x3 matrix") {
        val a = Matrix(
            Row(3, 5, 0),
            Row(2, -1, -7),
            Row(6, -1, 5)
        )

        a.minor(0, 0).shouldBe(-12.0)
        a.cofactor(0, 0).shouldBe(-12.0)
        a.minor(1, 0).shouldBe(25.0)
        a.cofactor(1, 0).shouldBe(-25.0)
    }

    test("Calculating the determinant of a 3x3 matrix") {
        val a = Matrix(
            Row(1, 2, 6),
            Row(-5, 8, -4),
            Row(2, 6, 4)
        )

        a.cofactor(0, 0).shouldBe(56.0)
        a.cofactor(0, 1).shouldBe(12.0)
        a.cofactor(0, 2).shouldBe(-46.0)
        a.determinant().shouldBe(-196.0)
    }

    test("Calculating the determinant of a 4x4 matrix") {
        val a = Matrix(
            Row(-2, -8, 3, 5),
            Row(-3, 1, 7, 3),
            Row(1, 2, -9, 6),
            Row(-6, 7, 7, -9)
        )

        a.cofactor(0, 0).shouldBe(690.0)
        a.cofactor(0, 1).shouldBe(447.0)
        a.cofactor(0, 2).shouldBe(210.0)
        a.cofactor(0, 3).shouldBe(51.0)
        a.determinant().shouldBe(-4071.0)
    }

    test("Testing an invertible matrix for invertibility") {
        val a = Matrix(
            Row(6, 4, 4, 4),
            Row(5, 5, 7, 6),
            Row(4, -9, 3, -7),
            Row(9, 1, 7, -6)
        )

        a.determinant().shouldBe(-2120.0)
        a.invertible().shouldBe(true)
    }

    test("Testing a noninvertible matrix for invertibility") {
        val a = Matrix(
            Row(-4, 2, -2, -3),
            Row(9, 6, 2, 6),
            Row(0, -5, 1, -5),
            Row(0, 0, 0, 0)
        )

        a.determinant().shouldBe(0.0)
        a.invertible().shouldBe(false)
    }

    test("Calculating the inverse of a matrix") {
        val a = Matrix(
            Row(-5, 2, 6, -8),
            Row(1, -5, 1, 8),
            Row(7, 7, -6, -7),
            Row(1, -3, 7, 4)
        )

        val b = a.inverse()

        a.determinant().shouldBe(532.0)
        a.cofactor(2, 3).shouldBe(-160.0)
        b[3, 2].near(-160.0 / 532.0).shouldBe(true)
        a.cofactor(3, 2).shouldBe(105.0)
        b[2, 3].near(105.0 / 532.0)
        b.shouldBe(Matrix(
            Row( 0.21805,  0.45113,  0.24060, -0.04511),
            Row(-0.80827, -1.45677, -0.44361,  0.52068),
            Row(-0.07895, -0.22368, -0.05263,  0.19737),
            Row(-0.52256, -0.81391, -0.30075,  0.30639)
        ))
    }

    test("Calculating the inverse of another matrix") {
        val a = Matrix(
            Row(8, -5, 9, 2),
            Row(7, 5, 6, 1),
            Row(-6, 0, 9, 6),
            Row(-3, 0, -9, -4)
        )

        a.inverse().shouldBe(Matrix(
            Row(-0.15385, -0.15385, -0.28205, -0.53846),
            Row(-0.07692,  0.12308,  0.02564,  0.03077),
            Row( 0.35897,  0.35897,  0.43590,  0.92308),
            Row(-0.69231, -0.69231, -0.76923, -1.92308)
        ))
    }

    test("Calculating the inverse of a third matrix") {
        val a = Matrix(
            Row(9, 3, 0, 9),
            Row(-5, -2, -6, -3),
            Row(-4, 9, 6, 4),
            Row(-7, 6, 6, 2)
        )

        a.inverse().shouldBe(Matrix(
            Row( -0.04074, -0.07778, 0.14444, -0.22222),
            Row( -0.07778, 0.03333, 0.36667, -0.33333),
            Row( -0.02901, -0.14630, -0.10926, 0.12963),
            Row( 0.17778, 0.06667, -0.26667, 0.33333),
        ))
    }

    test("Multiplying a matrix by its inverse") {
        val a = Matrix(
            Row(8, -5, 9, 2),
            Row(7, 5, 6, 1),
            Row(-6, 0, 9, 6),
            Row(-3, 0, -9, -4)
        )
        val b = Matrix(
            Row(9, 3, 0, 9),
            Row(-5, -2, -6, -3),
            Row(-4, 9, 6, 4),
            Row(-7, 6, 6, 2)
        )

        val c = a * b
        (c * b.inverse()).shouldBe(a)
    }

})