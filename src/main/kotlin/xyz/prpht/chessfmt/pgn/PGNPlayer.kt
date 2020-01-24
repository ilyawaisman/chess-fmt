package xyz.prpht.chessfmt.pgn

import xyz.prpht.chessfmt.anplayer.AlgebraicNotationPlayer
import java.io.File

object PGNPlayer {
    fun playAll(filename: String) {
        var gameId = 0
        File(filename).bufferedReader().lines().forEach { line ->
            if (line.isNotEmpty() && line[0] == '1') {
                println("Play game #${gameId++}")
                try {
                    AlgebraicNotationPlayer(line) { println(it) }.play()
                } catch (e: Throwable) {
                    println("Failed: ${e.message}")
                }
            }
        }
        println("Totally $gameId games played")
    }
}

fun main(args: Array<String>) {
    for (filename in args) {
        println("Processing file $filename")
        PGNPlayer.playAll(filename)
    }
}
