package y2018.d09

import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

data class Move(val elf: Int, val marble: Int)

fun main() {

    /*


    - play until one marble is worth = 71223
    - find elf with that score
    - calculate score of that elf


     */

    assert(32 == getHighScore(9, 25)!!.second)
    assert(8317 == getHighScore(10, 1618)!!.second)
    assert(146373 == getHighScore(13, 7999)!!.second)
//    assert(2764 == getHighScore(17, 1104)!!.second)
//    assert(54718 == getHighScore(21, 6111)!!.second)
//    assert(37305 == getHighScore(30, 5807)!!.second)

    val hs = getHighScore(455, 71223)
    println("Part 1: result = ${hs!!.second}")
    val elf = hs.first
    println("Elf = $elf")

    // 71223 -> 186 ms
    // 712230 -> 19968 ms
    // 712230 ->

    val time = measureTimeMillis {
        val x = getScoresForElf(455, 712230, elf)
        println("Part 2: result = $x")
    }
    println("# $time ms")
}


fun moves(players: Int, lastMarble: Int): Sequence<Move> {

    val playerTurns = sequence {
        var i = 1
        while (true) {
            yield(i)
            if (i == players) i = 1 else i += 1
        }
    }
    val marbleValues = sequence {
        for (i in 1..lastMarble) {
            yield(i)
        }
    }
    return playerTurns.zip(marbleValues).map { Move(it.first, it.second) }
}

class Board() {

    val circle = mutableListOf(0)
    var currentMarbleIndex = 0

    fun addMarble(marble: Int): Int {

        var score = 0

        val time = measureNanoTime {
        if (marble % 23 == 0) {
            score += marble
            val removePos = getRealIndex(currentMarbleIndex - 7)
            score += circle.removeAt(removePos)
            currentMarbleIndex = removePos

        } else {

            if (circle.size == 1) {
                circle.add(marble)
                currentMarbleIndex = 1
            } else {
                currentMarbleIndex = getRealIndex(currentMarbleIndex + 2)
                circle.add(currentMarbleIndex, marble)
            }
        }
        }

        if (marble % 10000 == 0) {
           // println("add time: $time")
        }

        //println("Board: $circle - current: $currentMarble")
        return score
    }

    fun getRealIndex(i: Int): Int {
        var r = when {
           i > circle.size -> i % circle.size
            i < 0 -> (circle.size + i)
            else -> i
        }
     //   println("circle: ${circle.size} $i -> $r")
        return r
    }

}

fun getScores(players: Int, lastMarble: Int): List<Pair<Int, Int>> {
    val board = Board()
    return moves(players, lastMarble)
            .map {
                Pair(it.elf, board.addMarble(it.marble))
            }
            .groupBy { it.first }
            .map {
                Pair(it.key, it.value.sumBy { it.second })
            }

}

fun getScoresForElf(players: Int, lastMarble: Int, elf: Int): Int {
    val board = Board()
    return moves(players, lastMarble)
            .map {
                Pair(it.elf, board.addMarble(it.marble))
            }
            .filter { it.first == elf }
            .map { it.second }
            .sum()

}

fun getHighScore(players: Int, lastMarble: Int): Pair<Int, Int>? {

    return getScores(players, lastMarble)
            .maxBy { it.second }
}
