package xyz.prpht.chessfmt.runtime

class Game {
    private val board: Board = InitBoard.create()
    private var move: Side = Side.White
    private var enPassant: Board.Square? = null

    fun applyRegMove(from: Board.Square, to: Board.Square, valid: RegMoveValidator.Result.Valid) {
        applyPieceShift(from, to, valid)
        afterMove()
    }

    fun applyShortCastling() {
        applyCastling(Castling[move, Castling.Kind.Short])
        afterMove()
    }

    fun applyLongCastling() {
        applyCastling(Castling[move, Castling.Kind.Long])
        afterMove()
    }

    private fun applyCastling(castling: Castling) {
        applyPieceShift(castling.kingFrom, castling.kingTo, RegMoveValidator.Result.simpleValid)
        applyPieceShift(castling.rookFrom, castling.rookTo, RegMoveValidator.Result.simpleValid)
    }

    private fun applyPieceShift(from: Board.Square, to: Board.Square, valid: RegMoveValidator.Result.Valid) {
        enPassant = null
        val piece = checkNotNull(board[from]) { "Empty 'from' square" }
        board[from] = null
        valid.victimSq?.let { board[it] = null }
        board[to] = piece
        enPassant = valid.enPassant
    }

    private fun afterMove() {
        move = !move
    }
}
