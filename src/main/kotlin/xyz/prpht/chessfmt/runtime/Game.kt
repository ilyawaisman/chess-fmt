package xyz.prpht.chessfmt.runtime

class Game {
    val board: Board = InitBoard.create()
    var side: Side = Side.White
        private set
    var enPassant: Board.Square? = null
        private set
    var moveId = 0 // zero means not started!
        private set

    fun start() {
        moveId = 1
    }

    fun applyRegMove(from: Board.Square, to: Board.Square, valid: RegMoveValidator.Result) {
        applyPieceShift(from, to, valid)
        afterMove()
    }

    fun applyShortCastling() {
        applyCastling(Castling[side, Castling.Kind.Short])
        afterMove()
    }

    fun applyLongCastling() {
        applyCastling(Castling[side, Castling.Kind.Long])
        afterMove()
    }

    private fun applyCastling(castling: Castling) {
        applyPieceShift(castling.kingFrom, castling.kingTo, RegMoveValidator.Result.simple)
        applyPieceShift(castling.rookFrom, castling.rookTo, RegMoveValidator.Result.simple)
    }

    private fun applyPieceShift(from: Board.Square, to: Board.Square, valid: RegMoveValidator.Result) {
        check(moveId > 0) { "Game must be started before making moves" }
        enPassant = null
        val piece = checkNotNull(board[from]) { "Empty 'from' square" }
        board[from] = null
        valid.victimSq?.let { board[it] = null }
        board[to] = piece
        enPassant = valid.enPassant
    }

    private fun afterMove() {
        side = !side
        if (side == Side.White)
            ++moveId
    }
}
