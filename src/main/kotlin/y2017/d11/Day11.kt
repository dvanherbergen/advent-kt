package y2017.d11

import java.io.File
import kotlin.math.abs


data class Coordinate(val q: Int, val r: Int) {
    fun move(direction: String): Coordinate {
        return when (direction) {
            "n" -> Coordinate(q, r - 1)
            "s" -> Coordinate(q, r + 1)
            "nw" -> Coordinate(q - 1, r)
            "se" -> Coordinate(q + 1, r)
            "ne" -> Coordinate(q + 1, r - 1)
            "sw" -> Coordinate(q - 1, r + 1)
            else -> throw IllegalArgumentException(direction)
        }
    }

    fun distanceTo(other: Coordinate): Int = (abs(other.q - q) + abs(other.q + other.r - q - r) + abs(other.r - r)) / 2
}

fun followPath(input: String): Pair<Int, Int> {
    val (location, maxDistance) = input.split(",")
            .fold(Pair(Coordinate(0, 0), 0)) { (coordinate, maxDistance), direction ->
                val newLocation = coordinate.move(direction)
                val distance = coordinate.distanceTo(Coordinate(0, 0))
                Pair(newLocation, if (distance > maxDistance) distance else maxDistance)
            }
    return Pair(location.distanceTo(Coordinate(0, 0)), maxDistance)
}

fun main() {
    assert(3 == followPath("ne,ne,ne").first)
    assert(0 == followPath("ne,ne,sw,sw").first)
    assert(2 == followPath("ne,ne,s,s").first)
    assert(3 == followPath("se,sw,se,sw,sw").first)

    val input = File("src/main/resources/y2017/d11/input.txt").readText()
    println("""Part 1: result = ${followPath(input).first}""")
    println("""Part 2: result = ${followPath(input).second}""")
}