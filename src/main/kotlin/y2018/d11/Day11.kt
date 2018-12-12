package y2018.d11

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun powerLevel(x: Int, y: Int, serial: Int): Int {
    val rackId = x + 10
    val r = ((((rackId * y) + serial) * rackId) % 1000)
    return ((r - (r % 100)) / 100) - 5
}

data class Square(val x: Int, val y: Int, val serial: Int, val size: Int = 3) {
    fun totalPower(): Int {
        return sequence {
            for (i in 1..size) {
                for (j in 1..size) {
                    yield(powerLevel(x + i - 1, y + j - 1, serial))
                }
            }
        }.sum()
    }
}

fun squares(serial: Int, size: Int): Sequence<Square> {
    return sequence {
        for (x in 1..300) {
            for (y in 1..300) {
                yield(Square(x, y, serial, size))
            }
        }
    }.filter { it.x + it.size <= 300 && it.y + it.size <= 300 }
}

fun main() {

    assert(4 == powerLevel(3,5,8))
    assert(-5 == powerLevel(122,79,57))
    assert(119 == Square(232, 251, 42, 12).totalPower())
    assert(113 == Square(90, 269, 18, 16).totalPower())

    val time = measureTimeMillis {

        val result1 = squares(7989, 3).maxBy { it.totalPower() }
        println("Part 1: result = ${result1!!.x},${result1!!.y}")

        val routines = (1..300).map {
            GlobalScope.async {
                squares(7980, it).maxBy { it.totalPower() }
            }
        }

        val result2 = runBlocking { routines.map { it.await() }}.filter { it != null }.maxBy { it!!.totalPower() }
        println("Part 2: result = ${result2!!.x},${result2!!.y},${result2!!.size}")
    }

    println("# $time ms to complete.")
}