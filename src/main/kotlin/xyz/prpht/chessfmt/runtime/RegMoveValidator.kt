package xyz.prpht.chessfmt.runtime

import kotlin.math.abs

class RegMoveValidator private constructor(
        private val board: Board,
        private val from: Board.Square,
        private val to: Board.Square,
        private val piece: Piece,
        private val prevEnPassant: Board.Square?,
        private val promotion: PieceKind?
) {
    private val vec = from..to
    private val vecAbs = Board.Vec(abs(vec.row), abs(vec.col))
    private var enPassant: Board.Square? = null

    companion object {
        fun isValid(
                board: Board,
                from: Board.Square,
                to: Board.Square,
                prevEnPassant: Board.Square?,
                promotion: PieceKind?
        ): Result {
            check(to != from) { "'from' and 'to' squares coincide" }
            val piece = checkNotNull(board[from]) { "'from' square is empty" }
            check(promotion != PieceKind.Pawn) { "'promotion' is ${PieceKind.Pawn}" }
            return RegMoveValidator(board, from, to, piece, prevEnPassant, promotion).isValid()
        }

        private val allowedVecAbsPawn = listOf(
                Board.Vec(1, 0),
                Board.Vec(2, 0),
                Board.Vec(1, 1)
        )

        private val allowedVecAbsKnight = listOf(
                Board.Vec(1, 2),
                Board.Vec(2, 1)
        )
    }

    fun isValid(): Result {
        if (needPromote(piece, to) != (promotion != null))
            return Result.Invalid

        val victimSq =
                if (to == prevEnPassant)
                    to.copy(row = to.row + piece.side.not().forw)
                else
                    to

        val victim = board[victimSq]
        if (victim?.side == piece.side)
            return Result.Invalid

        val isValid: Boolean = when (piece.kind) {
            PieceKind.Pawn -> isValidPawn()
            PieceKind.Knight -> isValidKnight()
            PieceKind.Bishop -> isValidBishop()
            PieceKind.Rook -> isValidRook()
            PieceKind.Queen -> isValidQueen()
            PieceKind.King -> isValidKing()
        }

        if (!isValid)
            return Result.Invalid

        return Result.Valid(victimSq, enPassant)
    }

    private fun isValidPawn(): Boolean {
        if (vec.row > 0 != piece.side.forw > 0)
            return false

        if (vecAbs !in allowedVecAbsPawn)
            return false

        if (vec.col != 0) // 1 || -1
            return board[to] != null

        if (vecAbs.row == 1)
            return true

        // vecAbs.row == 2
        val enPassant = from + vec.copy(row = vec.row / 2)
        if (board[enPassant] == null) {
            this.enPassant = enPassant
            return true
        }

        return false
    }

    private fun isValidKnight() = vecAbs in allowedVecAbsKnight

    private fun isValidBishop(): Boolean {
        if (vecAbs.row != vecAbs.col)
            return false

        return isWayFree()
    }

    private fun isValidRook(): Boolean {
        if (vecAbs.row != 0 && vecAbs.col != 0)
            return false

        return isWayFree()
    }

    private fun isValidQueen() = isValidBishop() || isValidRook()

    private fun isValidKing() = vecAbs.row <= 1 && vecAbs.col <= 1

    private fun isWayFree(): Boolean {
        val sign = vec.sign()
        var sq = from + sign
        while (sq != to) {
            if (board[sq] != null)
                return false

            sq += sign
        }
        return true
    }

    private fun needPromote(piece: Piece, to: Board.Square) = piece.kind == PieceKind.Pawn && to.row == piece.side.lastRow

    sealed class Result {
        data class Valid(val victimSq: Board.Square?, val enPassant: Board.Square?) : Result()
        object Invalid : Result()

        fun isValid() = this != Invalid

        companion object {
            val simpleValid = Valid(null, null)
        }
    }

    private val Side.lastRow
        get() = when (this) {
            Side.White -> 7
            Side.Black -> 0
        }

    private val Side.forw
        get() = when (this) {
            Side.White -> +1
            Side.Black -> -1
        }
}
