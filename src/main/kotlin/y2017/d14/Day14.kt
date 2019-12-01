package y2017.d14

import y2017.d10.denseHash

fun Char.toBits(): String = this.toString().toInt(radix = 16).toString(2).padStart(4, '0')


fun main() {

    val map = (0..127).map {
        denseHash("amgozmfv-$it")
                .asSequence()
                .map { it.toBits() }
                .joinToString("")
    }

    val usedSquares = map
            .joinToString("")
            .asSequence()
            .filter { it == '1' }
            .count()


    println("Part 1: result = $usedSquares")
}