package xyz.prpht.chessfmt.runtime

data class Piece(
        val side: Side,
        val kind: PieceKind
) {
    val symbol = symbols[side.ordinal][kind.ordinal]

    companion object {
        private val symbols = listOf(
                "♙♘♗♖♕♔",
                "♟♞♝♜♛♚"
        )
    }
}
