package y2018.d03

import java.io.File

data class Claim(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int) {

    constructor(input: String) : this(
            // no regex for me today :-)
            input.substring(1).substringBefore(" ").toInt(),
            input.substringAfter("@ ").substringBefore(",").toInt(),
            input.substringAfter(",").substringBefore(":").toInt(),
            input.substringAfter(": ").substringBefore("x").toInt(),
            input.substringAfter("x").toInt()
    )

    fun squares(): List<Pair<Int, Int>> {
        val s = ArrayList<Pair<Int, Int>>()
        for (i in left until (left + width)) {
            for (j in top until (top + height)) {
                s.add(Pair(i, j))
            }
        }
        return s
    }
}

fun main(args: Array<String>) {

    val overlaps = File("src/main/resources/y2018/d03/input.txt").readLines()
            .map { Claim(it) }
            .flatMap { it.squares() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .count()

    println("Part 1: result = $overlaps")
}