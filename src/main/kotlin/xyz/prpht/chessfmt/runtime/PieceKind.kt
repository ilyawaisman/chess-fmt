package xyz.prpht.chessfmt.runtime

enum class PieceKind(val symbol: Char) {
    Pawn('P'),
    Knight('N'),
    Bishop('B'),
    Rook('R'),
    Queen('Q'),
    King('K');

    companion object {
        val default = Pawn
    }
}
