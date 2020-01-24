package xyz.prpht.chessfmt.runtime

import kotlin.math.sign

class Board private constructor(
        private val storage: Array<Piece?>
) {
    constructor(): this(Array(dim * dim) { null })

    operator fun get(sq: Square) = storage[sq.id]

    operator fun set(sq: Square, piece: Piece?) {
        storage[sq.id] = piece
    }

    fun copy() = Board(storage.clone())

    companion object {
        const val dim = 8
        const val max = dim - 1
        private fun id(row: Int, col: Int) = row * dim + col
    }

    data class Square(
            val row: Int,
            val col: Int
    ) {
        init {
            check(row >= 0) { "'row' is negative: $row" }
            check(row < dim) { "'row' exceeds size: $row" }
            check(col >= 0) { "'col' is negative: $col" }
            check(col < dim) { "'col' exceeds size: $col" }
        }

        internal val id = id(row, col)
        operator fun rangeTo(o: Square) = Vec(o.row - row, o.col - col)
        operator fun plus(v: Vec) = Square(row + v.row, col + v.row)
    }

    data class Vec(
            val row: Int,
            val col: Int
    ) {
        fun sign() = Vec(
                row.sign,
                col.sign
        )
    }
}
