package y2018.d03

import java.io.File

fun Collection<Pair<Int, Int>>.containsNone(input: Collection<Pair<Int, Int>>): Boolean {
    for (c in input) {
        if (this.contains(c)) {
            return false
        }
    }
    return true
}

fun main(args: Array<String>) {

    val claims = File("src/main/resources/y2018/d03/input.txt").readLines()
            .map { Claim(it) }

    val overlappedSquares = claims
            .flatMap { it.squares() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .keys

   val c = claims.find {
        overlappedSquares.containsNone(it.squares())
    }

    println("Part 2: result = $c")
}