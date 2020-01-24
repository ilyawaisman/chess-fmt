package xyz.prpht.chessfmt.anplayer

import xyz.prpht.chessfmt.runtime.*

class AlgebraicNotationPlayer(
        private val historyAN: String,
        private val onPosition: (Game) -> Unit = {}
) {
    private val game = Game()

    fun play() {
        check(game.moveId == 0) { "Attempt to restart" }
        val moveANs = splitToMoveANs()

        onPosition(game)
        game.start()
        moveANs.forEach {
            makeMove(it)
            onPosition(game)
        }
    }

    private fun splitToMoveANs(): List<String> {
        val blocks = historyAN.split(" ")
        var inComment = false
        return blocks.filter {
            if (it.isEmpty())
                return@filter false

            if (it[0] == AlgebraicNotation.openCommentSymbol) {
                check(!inComment) { "Nested comments" }
                inComment = true
            }

            if (inComment) {
                if (it.last() == AlgebraicNotation.closeCommentSymbol)
                    inComment = false

                return@filter false
            }

            if (it.matches(AlgebraicNotation.moveIdRegex))
                return@filter false

            true
        }
    }

    private fun makeMove(moveAN: String) {
        if (moveAN.startsWith(AlgebraicNotation.longCastling)) {
            game.applyLongCastling()
            return
        }

        if (moveAN.startsWith(AlgebraicNotation.shortCastling)) {
            game.applyShortCastling()
            return
        }

        val eater = StringEater(moveAN)
        val piece = parsePiece(eater, moveAN)
        val fromTo = parseFromTo(eater, moveAN)
        val to = parseTo(fromTo, moveAN)
        val (fromRow: Int?, fromCol: Int?) = parseFrom(fromTo)
        val promotion: PieceKind? = parsePromotion(eater, moveAN)
        val move = deduceMove(piece, fromCol, fromRow, to, promotion, moveAN)

        game.applyRegMove(move.from, to, move.result)
    }

    private fun deduceMove(piece: Piece, fromCol: Int?, fromRow: Int?, to: Board.Square, promotion: PieceKind?, moveAN: String): RegMove {
        fun validate(sq: Board.Square) =
                game.board[sq].takeIf { it == piece }
                        .let { RegMoveValidator.isValid(game.board, game.side, sq, to, game.enPassant, promotion) }
                        ?.let { RegMove(sq, it) }

        val cands = when {
            fromCol != null && fromRow != null -> listOfNotNull(validate(Board.Square(fromRow, fromCol)))
            fromCol != null -> Board.range.mapNotNull { row -> validate(Board.Square(row, fromCol)) }
            fromRow != null -> Board.range.mapNotNull { col -> validate(Board.Square(fromRow, col)) }
            else -> Board.mapNotNull { from -> validate(from) }
        }

        check(cands.isNotEmpty()) { "Not found valid candidate (${fromRow ?: '*'}, ${fromCol ?: '*'}) -> $to for 'moveAN' = $moveAN, board: \n${game.board}" }
        check(cands.size <= 1) { "Found several candidates (${fromRow ?: '*'}, ${fromCol ?: '*'}) -> $to: ${cands.map { it.from }} for 'moveAN' = $moveAN, board: \n${game.board}" }
        return cands.first()
    }

    private fun parsePromotion(eater: StringEater, moveAN: String) =
            if (!eater.finished()) {
                check(eater.bite() == AlgebraicNotation.promotionSymbol) { "Expected promotion symbol in 'moveAN' = $moveAN" }
                val promotionPiece = eater.bite()
                checkNotNull(AlgebraicNotation.pieceBySymbol[promotionPiece]) { "Unknown symbol for promotion piece in 'moveAN' = $moveAN" }
            } else null

    private fun parseFrom(fromTo: ArrayList<Char>): Pair<Int?, Int?> {
        var fromRow: Int? = null
        var fromCol: Int? = null

        if (fromTo.size == 3) {
            val c = fromTo[0]
            if (AlgebraicNotation.isValidRow(c))
                fromRow = AlgebraicNotation.row(c)
            else
                fromCol = AlgebraicNotation.col(c)
        }

        if (fromTo.size == 4) {
            fromCol = AlgebraicNotation.col(fromTo[0])
            fromRow = AlgebraicNotation.row(fromTo[1])
        }
        return Pair(fromRow, fromCol)
    }

    private fun parseTo(fromTo: ArrayList<Char>, moveAN: String): Board.Square {
        val toRow = fromTo.last()
        val toCol = fromTo[fromTo.lastIndex - 1]
        check(AlgebraicNotation.isValidRow(toRow)) { "Unexpected last coord 'moveAN' = $moveAN, row expected, but was $toRow" }
        check(AlgebraicNotation.isValidCol(toCol)) { "Unexpected pre-last coord 'moveAN' = $moveAN, col expected, but was $toCol" }
        return Board.Square(AlgebraicNotation.row(toRow), AlgebraicNotation.col(toCol))
    }

    private fun parseFromTo(eater: StringEater, moveAN: String): ArrayList<Char> {
        val fromTo = ArrayList<Char>(4)
        while (!eater.finished()) {
            val c = eater.peek()
            if (AlgebraicNotation.isValidCoord(c))
                fromTo.add(eater.bite())
            else if (c in AlgebraicNotation.takeSymbols)
                eater.bite()
            else
                break
        }

        check(fromTo.size in 2..4) { "Unexpected count of coords in 'moveAN' = $moveAN (${fromTo.size} found)" }
        return fromTo
    }

    private fun parsePiece(eater: StringEater, moveAN: String): Piece {
        val pieceKind =
                if (eater.peek().isUpperCase())
                    checkNotNull(AlgebraicNotation.pieceBySymbol[eater.bite()]) { "Unknown piece symbol in 'moveAN' = $moveAN" }
                else
                    AlgebraicNotation.defaultPiece

        return Piece(game.side, pieceKind)
    }

    private data class RegMove(
            val from: Board.Square,
            val result: RegMoveValidator.Result
    )
}
