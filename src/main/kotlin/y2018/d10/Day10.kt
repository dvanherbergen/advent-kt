import io.reactivex.rxkotlin.toObservable
import java.io.File

data class Star(val x:Int, val y: Int, val vx: Int, val vy: Int) {
    fun move(): Star = Star(x + vx, y + vy, vx, vy)
}

data class Constellation(val second: Int, val stars: List<Star>) {
    fun minX() = stars.minBy { it.x }!!.x
    fun maxX() = stars.maxBy { it.x }!!.x
    fun minY() = stars.minBy { it.y }!!.y
    fun maxY() = stars.maxBy { it.y }!!.y
}

fun main() {

    val regex = ".*<(.*),(.*)>.*<(.*),(.*)>.*".toRegex()

    val stars = File("src/main/resources/y2018/d10/input.txt").readLines()
            .map { regex.matchEntire(it)!!.destructured
                    .let { (a, b, c, d) -> Star(a.trim().toInt(), b.trim().toInt(), c.trim().toInt() ,d.trim().toInt()) }}

    val finalConstellation = (1..100000).toObservable()
            .scan(Constellation(0, stars)) { acc, second -> Constellation(second, acc.stars.map { it.move() }) }
            .filter { it.maxY() - it.minY() < 19 }
            .blockingFirst()

    fun toPrintableRow(starsInRow: List<Star>): String {
       return (finalConstellation.minX()..finalConstellation.maxX())
               .joinToString("") { x ->  if (starsInRow.find { it.x == x } == null) " " else "#" }
    }

    val result = finalConstellation.stars
            .groupBy { it.y }
            .toSortedMap()
            .map { toPrintableRow(it.value) }
            .joinToString("\n")

    println("Found final constellation in ${finalConstellation.second} seconds.\n")
    println(result)
}