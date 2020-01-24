package xyz.prpht.chessfmt.anplayer

class StringEater(private val s: String) {
    var head = 0

    fun peek() = s[head]

    fun bite() = s[head++]

    fun finished() = head >= s.length
}
