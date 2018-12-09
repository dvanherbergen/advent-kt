package y2018.d09

data class Move(val elf: Int, val marble: Int)

fun main() {

    /*


    - play until one marble is worth = 71223
    - find elf with that score
    - calculate score of that elf


     */

    assert(32 == calcScores(9, 25))
    assert(8317 == calcScores(10, 1618))
    assert(146373 == calcScores(13, 7999))
    assert(2764 == calcScores(17, 1104))
    assert(54718 == calcScores(21, 6111))
    assert(37305 == calcScores(30, 5807))

    println("Part 1: result = ${calcScores(455, 71223)}")
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
    var currentMarble = 0

    fun addMarble(marble: Int): Int {


        var score = 0

        if (marble % 23 == 0) {
            // TODO special handling for 23
            score += marble

            val removePos = getRealIndex(circle.indexOf(currentMarble) - 7)
            score += circle.removeAt(removePos)
            currentMarble = circle[removePos]

        } else {

            if (circle.size == 1) {
                circle.add(marble)
            } else {
                circle.add(getRealIndex(circle.indexOf(currentMarble) + 2), marble)
            }
            currentMarble = marble
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


fun calcScores(players: Int, lastMarble: Int): Int {

    val board = Board()

    val player = moves(players, lastMarble)

            .map {
           //     println(it)
                Pair(it.elf, board.addMarble(it.marble))
            }
            .groupBy { it.first }

            .map {
                Pair(it.key, it.value.sumBy { it.second })
            }
            .maxBy { it.second }


    return player!!.second

/*

1=[(1, 0), (1, 0), (1, 0)]
2=[(2, 0), (2, 0), (2, 0)]
3=[(3, 0), (3, 0), (3, 0)]
4=[(4, 0), (4, 0), (4, 0)]
5=[(5, 0), (5, 0), (5, 32)]
6=[(6, 0), (6, 0), (6, 0)]
7=[(7, 0), (7, 0), (7, 0)]
8=[(8, 0), (8, 0)]
9=[(9, 0), (9, 0)]
 */


}
