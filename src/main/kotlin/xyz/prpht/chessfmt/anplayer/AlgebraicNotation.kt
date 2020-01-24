package xyz.prpht.chessfmt.anplayer

import xyz.prpht.chessfmt.runtime.PieceKind

object AlgebraicNotation {
    const val shortCastling = "O-O"
    const val longCastling = "O-O-O"

    val defaultPiece = PieceKind.Pawn
    val pieceBySymbol: Map<Char, PieceKind> = enumValues<PieceKind>().associateBy { it.name[0] } + ('N' to PieceKind.Knight)

    fun row(c: Char): Int {
        check(isValidRow(c))
        return c - '1'
    }

    fun col(c: Char): Int {
        check(isValidCol(c))
        return c - 'a'
    }

    fun isValidRow(c: Char) = c in '1'..'8'
    fun isValidCol(c: Char) = c in 'a'..'h'

    fun isValidCoord(c: Char) = isValidRow(c) || isValidCol(c)

    val takeSymbols = setOf('x', ':')
    const val promotionSymbol = '='
}
