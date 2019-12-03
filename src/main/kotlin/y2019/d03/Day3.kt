package y2019.d03

import java.io.File
import kotlin.math.absoluteValue

data class Coordinate(val x: Int, val y: Int) {
    fun move(direction: String): List<Coordinate> {
        val steps = direction.substring(1).toInt()
        return when (direction[0]) {
            'R' -> (1..steps).map { Coordinate(this.x + it, this.y) }
            'U' -> (1..steps).map { Coordinate(this.x, this.y + it) }
            'L' -> (1..steps).map { Coordinate(this.x - it, this.y) }
            'D' -> (1..steps).map { Coordinate(this.x, this.y - it) }
            else -> throw Exception("what happened?")
        }
    }
}

val testInput1 = listOf<String>("R8,U5,L5,D3", "U7,R6,D4,L4")
val testInput2 = listOf<String>("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83")
val testInput3 = listOf<String>("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")

val addSteps = fun (coordinates: List<Coordinate>, movement: String): List<Coordinate> = coordinates + coordinates.last().move(movement)

fun String.toCoordinates() : List<Coordinate> {
    return this.split(',')
            .fold(listOf(Coordinate(0, 0)), addSteps)
}

fun List<String>.getClosestIntersection(): Int {
    return this.flatMap { it.toCoordinates().distinct() }
            .groupBy { it }
            .filter { it.value.size > 1 && it.key != Coordinate(0, 0)}
            .map { it.key.x.absoluteValue + it.key.y.absoluteValue }
            .min() ?: 0
}

fun List<String>.getClosestIntersectionBySteps(): Int {

    return this.flatMap {
                it.toCoordinates()
                        .mapIndexed { index, coordinate -> Pair(coordinate, index) }
                        .groupBy { it.first }
                        .map { Pair(it.key, it.value[0].second) }
    }
            .groupBy { it.first }
            .filter { it.value.size > 1 && it.key != Coordinate(0, 0)}
            .map { it.value.map { it.second }.sum() }
            .min() ?: 0
}

fun main() {
    assert(testInput1.getClosestIntersection() == 6)
    assert(testInput2.getClosestIntersection() == 159)
    assert(testInput3.getClosestIntersection() == 135)
    println("Part 1: ${File("src/main/resources/y2019/d03/input.txt").readLines().getClosestIntersection()}")

    assert(testInput1.getClosestIntersectionBySteps() == 30)
    assert(testInput2.getClosestIntersectionBySteps() == 610)
    assert(testInput3.getClosestIntersectionBySteps() == 410)
    println("Part 2: ${File("src/main/resources/y2019/d03/input.txt").readLines().getClosestIntersectionBySteps()}")
}