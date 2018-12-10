package y2018.d09

fun main() {
    assert(8317L == getHighScore(10, 1618))
    assert(37305L == getHighScore(30, 5807))

    println("Part 1: result = ${getHighScore(455, 71223)}")
    println("Part 2: result = ${getHighScore(455, 7122300)}")
}

fun getHighScore(players: Int, lastMarble: Int): Long {
    val board = Board()
    return (1..players).asSequence()
            .repeat()
            .zip(IntRange(1, lastMarble).asSequence())
            .map { Pair(it.first, board.addMarble(it.second)) }
            .groupBy { it.first }
            .map { Pair(it.key, it.value.toList().map { it.second }.sum()) }
            .maxBy { it.second }
            ?.second ?: 0
}

class Board {

    var currentMarble = Marble()

    fun addMarble(marble: Int): Long {
        return if (marble % 23 == 0) {
            val marbleToTake = currentMarble.getPrev(7)
            currentMarble = marbleToTake.next
            removeMarble(marbleToTake)
            (marble + marbleToTake.value).toLong()
        } else {
            currentMarble = Marble(marble, currentMarble.next)
            0
        }
    }

    private fun removeMarble(marble: Marble) {
        marble.prev.next = marble.next
        marble.next.prev = marble.prev
    }
}

class Marble() {

    var value = 0
    var prev: Marble = this
    var next: Marble = this

    constructor(value: Int, prev: Marble): this() {
        this.value = value
        this.next = prev.next
        prev.next.prev = this
        prev.next = this
        this.prev = prev
    }

    fun getPrev(step: Int): Marble {
        var node = this
        for (i in 1..step) {
            node = node.prev
        }
        return node
    }
}

fun <T> Sequence<T>.repeat() : Sequence<T> = sequence {
    while(true) yieldAll(this@repeat)
}