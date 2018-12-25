package y2018.d25

import java.io.File
import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int, val t: Int)
typealias Constellation = List<Point>

fun Point.inRangeOf(other: Point): Boolean = abs(x - other.x) + abs(y - other.y) + abs(z - other.z) + abs(t - other.t) < 4
fun Point.canJoin(constellation: Constellation): Boolean = constellation.find { it.inRangeOf(this) } != null

fun extractConstellation(constellation: Constellation, points: List<Point>): Pair<Constellation, List<Point>> {
    if (constellation.isEmpty()) {
        return extractConstellation(constellation + points.first(), points.drop(1))
    }
    val groups = points.partition { it.canJoin(constellation) }
    return if (groups.first.isEmpty()) {
        Pair(constellation, groups.second)
    } else {
        extractConstellation(constellation + groups.first, groups.second)
    }
}

fun extractConstellations(points: List<Point>): List<Constellation> {
    if (points.isEmpty()) {
        return emptyList()
    }
    val (constellation, remaining) = extractConstellation(emptyList(), points)
    return listOf(constellation) + extractConstellations(remaining)
}

fun main() {
    val points = File("src/main/resources/y2018/d25/input.txt").readLines()
            .map { it.split(",").let { (x, y, z, t) -> Point(x.toInt(), y.toInt(), z.toInt(), t.toInt()) } }

    val constellations = extractConstellations(points)
    println("Part 1: result = ${constellations.size}")
    println("Part 2: result = nothing... no more puzzles :-(")
}
