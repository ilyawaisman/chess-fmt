package xyz.prpht.chessfmt.runtime

object InitBoard {
    fun initRow(side: Side) = when (side) {
        Side.White -> 0
        Side.Black -> Board.max
    }

    private val firstRow = listOf(
            PieceKind.Rook,
            PieceKind.Knight,
            PieceKind.Bishop,
            PieceKind.Queen,
            PieceKind.King,
            PieceKind.Bishop,
            PieceKind.Knight,
            PieceKind.Rook
    )

    val kingCol = firstRow.withIndex().find { it.value == PieceKind.King }!!.index
    val rookLeftCol = firstRow.withIndex().find { it.value == PieceKind.Rook }!!.index
    val rookRightCol = firstRow.withIndex().findLast { it.value == PieceKind.Rook }!!.index

    private val board = Board()

    init {
        (0..Board.dim).forEach { col ->
            board[Board.Square(initRow(Side.White), col)] = Piece(Side.White, firstRow[col])
            board[Board.Square(initRow(Side.White) + 1, col)] = Piece(Side.White, PieceKind.Pawn)
            board[Board.Square(initRow(Side.Black) - 1, col)] = Piece(Side.Black, PieceKind.Pawn)
            board[Board.Square(initRow(Side.Black), col)] = Piece(Side.Black, firstRow[col])
        }
    }

    fun create() = board.copy()
}
