package y2018.d06

import java.io.File

data class Coordinate(val x: Int, val y: Int) {

    fun distanceTo(c: Coordinate): Int {
        return this.x.difference(c.x) + this.y.difference(c.y)
    }

    private fun Int.difference(i: Int): Int {
        return if (this > i) this - i else i - this
    }
}



fun main(args: Array<String>) {

    val coordinates = File("src/main/resources/y2018/d06/input.txt").readLines().map{ Coordinate(it.substringBefore(",").trim().toInt(), it.substringAfter(",").trim().toInt())}


    val gridBoundaries = coordinates.asSequence()
            .fold(Coordinate(0,0)) { br, c ->
                Coordinate(if (c.x > br.x) c.x + 100 else br.x, if (c.y > br.y) c.y + 100 else br.y)
            }

    val grid1 = HashMap<Coordinate, Coordinate>()

    for (x in 0..gridBoundaries.x) {
        for (y in 0..gridBoundaries.y) {
            val nc = Coordinate(x, y)
            if (!isUndetermined(nc, coordinates)) {
                grid1.put(nc, getClosestCoordinate(nc, coordinates))
            }
        }
    }

    val grid2 = HashMap<Coordinate, Coordinate>()

    for (x in -100..(gridBoundaries.x+100)) {
        for (y in -100..(gridBoundaries.y+100)) {
            val nc = Coordinate(x, y)
            if (!isUndetermined(nc, coordinates)) {
                grid2.put(nc, getClosestCoordinate(nc, coordinates))
            }
        }
    }

    val m1 = grid1.asSequence()
            .groupingBy { it.value }
            .eachCount().entries
    val m2 = grid2.asSequence()
            .groupingBy { it.value }
            .eachCount().entries

    val size = m1.zip(m2).filter { it.first.value == it.second.value }
            .map { it.first }
            .maxBy { it.value }

    println("Part 1: result = $size")




}



fun isUndetermined(nc: Coordinate, coordinates: List<Coordinate>): Boolean {
    return 0 == coordinates.asSequence()
            .map{ it.distanceTo(nc) }
            .sorted()
            .take(2)
            .reduce { acc, i -> acc - i}
}

fun getClosestCoordinate(nc: Coordinate, coordinates: List<Coordinate>): Coordinate {
    return coordinates.asSequence()
            .map{ Pair(it, it.distanceTo(nc)) }
            .minBy { it.second }?.first!!
}



