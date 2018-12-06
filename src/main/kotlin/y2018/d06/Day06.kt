package y2018.d06

import java.io.File
import kotlin.math.abs
import kotlin.math.max

data class Coordinate(val x: Int, val y: Int) {
    fun distanceTo(c: Coordinate): Int {
        return abs(this.x - c.x) + abs(this.y - c.y)
    }
}

fun gridPoints(x: Int, y: Int, xx: Int, yy: Int): Sequence<Coordinate> {
    return sequence {
        for (i in x..xx) {
            for (j in y..yy) {
                yield(Coordinate(i, j))
            }
        }
    }
}

fun Sequence<Coordinate>.hasEqualDistanceTo(nc: Coordinate): Boolean {
    return 0 == this
            .map { it.distanceTo(nc) }
            .sorted()
            .take(2)
            .reduce { acc, i -> acc - i }
}

fun Sequence<Coordinate>.closestTo(nc: Coordinate): Coordinate {
    return this
            .map { Pair(it, it.distanceTo(nc)) }
            .minBy { it.second }?.first!!
}

fun main() {

    val coordinates = File("src/main/resources/y2018/d06/input.txt").readLines()
            .map { Coordinate(it.substringBefore(",").trim().toInt(), it.substringAfter(",").trim().toInt()) }
            .asSequence()

    val gridEdge = coordinates.asSequence()
            .fold(Coordinate(0, 0)) { acc, c -> Coordinate(max(c.x, acc.x), max(c.y, acc.y)) }

    val areas = gridPoints(0, 0, gridEdge.x, gridEdge.y)
            .filter { !coordinates.hasEqualDistanceTo(it) }
            .map { Pair(it, coordinates.closestTo(it)) }
            .groupingBy { it.second }
            .eachCount()
            .entries

    val areas2 = gridPoints(-100, -100, gridEdge.x + 100, gridEdge.y + 100)
            .filter { !coordinates.hasEqualDistanceTo(it) }
            .map { Pair(it, coordinates.closestTo(it)) }
            .groupingBy { it.second }
            .eachCount()
            .entries

    val largestFiniteRegion = areas.zip(areas2)
            .filter { it.first.value == it.second.value }
            .map { it.first }
            .maxBy { it.value }?.value

    println("Part 1: result = $largestFiniteRegion")

    val regionSize = gridPoints(0, 0, gridEdge.x, gridEdge.y)
            .map { c -> coordinates.map { c.distanceTo(it) }.sum() }
            .filter { it < 10000 }
            .count()

    println("Part 2: result = $regionSize")
}





