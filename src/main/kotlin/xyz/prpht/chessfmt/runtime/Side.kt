package xyz.prpht.chessfmt.runtime

enum class Side {
    White, Black;

    operator fun not() = when(this) {
        White -> Black
        Black -> White
    }
}
