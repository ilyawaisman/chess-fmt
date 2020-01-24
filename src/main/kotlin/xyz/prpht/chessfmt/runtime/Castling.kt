package xyz.prpht.chessfmt.runtime

class Castling private constructor(
        side: Side,
        kind: Kind
) {
    private val row = InitBoard.initRow(side)
    val kingFrom = Board.Square(row, InitBoard.kingCol)

    val kingTo = kingFrom.copy(
            col = kingFrom.col + when (kind) {
                Kind.Short -> +2
                Kind.Long -> -2
            }
    )

    val rookFrom = when (kind) {
        Kind.Short -> Board.Square(row, InitBoard.rookRightCol)
        Kind.Long -> Board.Square(row, InitBoard.rookLeftCol)
    }

    val rookTo = kingTo.copy(
            col = kingTo.col + when (kind) {
                Kind.Short -> -1
                Kind.Long -> +1
            }
    )

    enum class Kind {
        Short, Long
    }

    companion object {
        private val universe = enumValues<Side>().map { side ->
            side to enumValues<Kind>().map { kind ->
                kind to Castling(side, kind)
            }.toMap()
        }.toMap()

        @Suppress("MapGetWithNotNullAssertionOperator")
        operator fun get(side: Side, kind: Kind): Castling = universe[side]!![kind]!!
    }
}
