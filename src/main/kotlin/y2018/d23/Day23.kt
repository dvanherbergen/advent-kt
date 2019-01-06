package y2018.d23

import java.io.File
import kotlin.math.absoluteValue

data class Nanobot(val x: Long, val y: Long, val z: Long, val r: Long)
data class Point(val x: Long, val y: Long, val z: Long, val count: Int)

fun Nanobot.distanceTo(other: Nanobot): Long = (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue + (this.z - other.z).absoluteValue
fun Point.distanceTo(x: Long, y: Long, z: Long): Long = (this.x - x).absoluteValue + (this.y - y).absoluteValue + (this.z - z).absoluteValue
fun Nanobot.contains(x: Long, y: Long, z: Long): Boolean = ((this.x - x).absoluteValue + (this.y - y).absoluteValue + (this.z - z).absoluteValue) <= this.r

fun main() {

    val regex = ".*<(.*),(.*),(.*)>, r=(.*)".toRegex()
    val nanobots = File("src/main/resources/y2018/d23/input.txt").readLines()
            .map {
                regex.matchEntire(it)!!.destructured.let { (x, y, z, r) -> Nanobot(x.toLong(), y.toLong(), z.toLong(), r.toLong()) }
            }

    val strongest = nanobots.maxBy { it.r }
    val inRangeOfStrongest = nanobots.filter { it.distanceTo(strongest!!) <= strongest!!.r }

    println("Part 1: result = ${inRangeOfStrongest.size}")

    val minX = nanobots.minBy { it.x }!!.x
    val minY = nanobots.minBy { it.y }!!.y
    val minZ = nanobots.minBy { it.z }!!.z
    val maxX = nanobots.maxBy { it.x }!!.x
    val maxY = nanobots.maxBy { it.y }!!.y
    val maxZ = nanobots.maxBy { it.z }!!.z

    fun findClosestPoint(x: Long, xx: Long, y: Long, yy: Long, z: Long, zz: Long, scale: Int, bucketSize: Long, minCount: Int): Point {

        // scale the universe down and do a rough scan to find the closest points.
        // then rinse and repeat...

        val shrunkenBots = nanobots.map { Nanobot(it.x / scale, it.y / scale, it.z / scale, it.r / scale) }
        fun findBotsInRange(x: Long, y: Long, z: Long): List<Nanobot> = shrunkenBots.filter { it.contains(x, y, z) }
        fun scaled(num: Long): Long = num / scale

        val pointsToSearch = sequence {
            for (ix in scaled(x)..scaled(xx) step bucketSize) {
                for (iy in scaled(y)..scaled(yy) step bucketSize) {
                    for (iz in scaled(z)..scaled(zz) step bucketSize) {
                        yield(Point(ix, iy, iz, 0))
                    }
                }
            }
        }

        val matches = pointsToSearch
                .map { Point(it.x, it.y, it.z, findBotsInRange(it.x, it.y, it.z).size) }
                .filter { it.count >= minCount }

        val max = matches.maxBy { it.count }!!.count
        val bestMatch = matches.filter { it.count == max }.sortedBy { it.distanceTo(0, 0, 0) }.first()
        println("Max found using scale $scale ($bucketSize): $max")

        return if (scale > 1) {
            findClosestPoint(
                    (bestMatch.x - bucketSize) * scale, (bestMatch.x + bucketSize) * scale,
                    (bestMatch.y - bucketSize) * scale, (bestMatch.y + bucketSize) * scale,
                    (bestMatch.z - bucketSize) * scale, (bestMatch.z + bucketSize) * scale, scale / 10, bucketSize, max)
        } else if (scale == 1 && bucketSize > 1L) {
            findClosestPoint(
                    (bestMatch.x - bucketSize) * scale, (bestMatch.x + bucketSize) * scale,
                    (bestMatch.y - bucketSize) * scale, (bestMatch.y + bucketSize) * scale,
                    (bestMatch.z - bucketSize) * scale, (bestMatch.z + bucketSize) * scale, scale, 1, max)
        } else {
            bestMatch
        }
    }

    val closest = findClosestPoint(minX, maxX, minY, maxY, minZ, maxZ, 1_000_000, 10, 800)
    println("Part 2: Closest point = $closest, distance= ${closest.distanceTo(0, 0, 0)}")
}