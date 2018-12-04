import java.io.File
import kotlin.system.measureTimeMillis

typealias Coordinate = Pair<Int, Int>

data class Claim(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int) {

    fun squares(): List<Coordinate> {
        val s = ArrayList<Coordinate>()
        for (i in left until (left + width)) {
            for (j in top until (top + height)) {
                s.add(Coordinate(i, j))
            }
        }
        return s
    }
}

fun main(args: Array<String>) {

    val timeElapsed = measureTimeMillis {

        val regex = """#(\d*) @ (\d*),(\d*): (\d*)x(\d*)""".toRegex()

        val claims = File("src/main/resources/y2018/d03/input.txt").readLines()
                .map { regex.matchEntire(it)!!.destructured
                        .let { (a, b, c, d, e) -> Claim(a.toInt(), b.toInt(), c.toInt(), d.toInt(), e.toInt()) }}
        val overlaps = claims
                .flatMap { it.squares() }
                .groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }

        println("Part 1: result = ${overlaps.count()}")

        val claim = claims.find {
            overlaps.keys.containsNone(it.squares())
        }

        println("Part 2: result = $claim")
    }

    println("Took $timeElapsed ms.")
}

fun <T> Collection<T>.containsNone(input: Collection<T>): Boolean {
    return input.find { this.contains(it) } == null
}