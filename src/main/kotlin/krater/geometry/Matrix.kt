package krater.geometry

import kotlin.math.cos
import kotlin.math.sin

class Row(val values: DoubleArray) {
    constructor(vararg numbers: Number): this(numbers.map { it.toDouble() }.toDoubleArray())
    constructor(numbers: List<Number>): this(numbers.map { it.toDouble() }.toDoubleArray())

    operator fun get(col: Int) = values[col]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Row

        return values.size == other.values.size && !(values.zip(other.values).any { !it.first.near(it.second) })
    }

    override fun hashCode(): Int {
        return values.map(Double::nearHash).toIntArray().contentHashCode()
    }

    fun dot(other: Row) = values.zip(other.values).map { it.first * it.second }.sum()

    override fun toString(): String {
        return values.contentToString()
    }
}

val IDENTITY_4X4_MATRIX = Matrix(
    Row(1, 0, 0, 0),
    Row(0, 1, 0, 0),
    Row(0, 0, 1, 0),
    Row(0, 0, 0, 1)
)

class Matrix(vararg val rows: Row) {
    constructor(rows: List<Row>): this(*rows.toTypedArray())

    operator fun get(row: Int, col: Int) = rows[row][col]
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (!rows.contentEquals(other.rows)) return false

        return true
    }

    override fun hashCode(): Int {
        return rows.contentHashCode()
    }

    private fun columnAsRow(col: Int) = Row(rows.map { it[col] })

    operator fun times(other: Matrix): Matrix = Matrix(rows.map { row ->
        Row(row.values.mapIndexed { index, _ -> row.dot(other.columnAsRow(index)) })
    })

    private fun rowAsTuple(row: Row) = Tuple(row[0], row[1], row[2], row[3])

    operator fun times(tuple: Tuple): Tuple {
        return Tuple(
            rowAsTuple(rows[0]).dot(tuple),
            rowAsTuple(rows[1]).dot(tuple),
            rowAsTuple(rows[2]).dot(tuple),
            rowAsTuple(rows[3]).dot(tuple),
        )
    }

    override fun toString(): String {
        return "Matrix(${rows.contentToString()})"
    }

    fun transpose() = Matrix(rows.mapIndexed { index, _ -> columnAsRow(index) })
    fun determinant(): Double = if (rows.size == 2) rows[0][0] * rows[1][1] - rows[0][1] * rows[1][0]
    else rows[0].values.foldIndexed(0.0) { index, det, value -> det + value * cofactor(0, index) }

    fun subMatrix(row: Int, col: Int) = Matrix(
        rows.filterIndexed { index, _ -> index != row }
            .map { Row(it.values.filterIndexed { index, _ -> index != col }) })

    fun minor(row: Int, col: Int) = subMatrix(row, col).determinant()
    fun cofactor(row: Int, col: Int) = if ((row + col) % 2 == 1) -minor(row, col) else minor(row, col)
    fun invertible() = !determinant().near(0.0)
    fun inverse(): Matrix {
        val det = determinant()
        return Matrix(
            rows.mapIndexed { rowIndex, row ->
                Row(row.values.mapIndexed { colIndex, _ -> cofactor(rowIndex, colIndex) / det })}
        ).transpose()
    }

    fun rotateX(r: Double) = rotationX(r) * this
    fun rotateY(r: Double) = rotationY(r) * this
    fun rotateZ(r: Double) = rotationZ(r) * this
    fun translate(x: Number, y: Number, z: Number) = translation(x, y, z) * this
    fun scale(x: Number, y: Number, z: Number) = scaling(x, y, z) * this
    fun shear(xOnY: Number, xOnZ: Number, yOnX: Number, yOnZ: Number, zOnX: Number, zOnY: Number)
            = shearing(xOnY, xOnZ, yOnX, yOnZ, zOnX, zOnY) * this
}

fun translation(x: Number, y: Number, z: Number) = Matrix(
    Row(1, 0, 0, x),
    Row(0, 1, 0, y),
    Row(0, 0, 1, z),
    Row(0, 0, 0, 1)
)

fun scaling(x: Number, y: Number, z: Number) = Matrix(
    Row(x, 0, 0, 0),
    Row(0, y, 0, 0),
    Row(0, 0, z, 0),
    Row(0, 0, 0, 1)
)

fun rotationX(r: Double) = Matrix(
    Row(1, 0, 0, 0),
    Row(0, cos(r), -sin(r), 0),
    Row(0, sin(r), cos(r), 0),
    Row(0, 0, 0, 1)
)

fun rotationY(r: Double) = Matrix(
    Row(cos(r), 0, sin(r), 0),
    Row(0, 1, 0, 0),
    Row(-sin(r), 0, cos(r), 0),
    Row(0, 0, 0, 1)
)

fun rotationZ(r: Double) = Matrix(
    Row(cos(r), -sin(r), 0, 0),
    Row(sin(r), cos(r), 0, 0),
    Row(0, 0, 1, 0),
    Row(0, 0, 0, 1)
)

fun shearing(xOnY: Number, xOnZ: Number, yOnX: Number, yOnZ: Number, zOnX: Number, zOnY: Number) = Matrix(
    Row(1, xOnY, xOnZ, 0),
    Row(yOnX, 1, yOnZ, 0),
    Row(zOnX, zOnY, 1, 0),
    Row(0, 0, 0, 1)
)