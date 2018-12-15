package y2018.d18

import io.reactivex.rxkotlin.toObservable
import java.io.File

typealias LandOfTheElves = List<List<Char>>

fun main() {

    val landOfTheElves: LandOfTheElves = File("src/main/resources/y2018/d18/input.txt").readLines()
            .map { it.asSequence().toList() }
            .toList()

    val lumberCollections = (1..10_000).toObservable().scan(landOfTheElves) { land, _ ->
        land.mapIndexed { y, row ->
            row.mapIndexed { x, acre ->
                when {
                    acre == '.' && land.countAdjacentLots(x, y, '|') >= 3 -> '|'
                    acre == '|' && land.countAdjacentLots(x, y, '#') >= 3 -> '#'
                    acre == '#' && land.countAdjacentLots(x, y, '#') >= 1 && land.countAdjacentLots(x, y, '|') >= 1 -> '#'
                    acre == '#' -> '.'
                    else -> acre
                }
            }
        }.toList()
    }.map { it.countAll('|') * it.countAll('#') }

    println("Part 1: result = ${lumberCollections.take(11).blockingLast()}")

    val part2ResourceValues = lumberCollections.take(500).toList().blockingGet()

    println("Part 2: result = ${findPattern(part2ResourceValues).calculateValueAt(1_000_000_000)}")
}

fun LandOfTheElves.countAdjacentLots(x: Int, y: Int, type: Char): Int {
    fun count(xx: Int, yy: Int): Int {
        return if (xx < 0 || xx >= this[0].size || yy < 0 || yy >= this.size || this[yy][xx] != type) 0 else 1
    }
    return  count(x, y - 1) + count(x + 1, y - 1) + count(x + 1, y) + count(x + 1, y + 1) +
            count(x, y + 1) + count(x - 1, y + 1) + count(x - 1, y) + count(x - 1, y - 1)
}

fun LandOfTheElves.countAll(type: Char): Int = this.map { it.map { if (it == type) 1 else 0 }.sum() }.sum()

data class Pattern<T>(val index: Int, val values: List<T>) {
    fun calculateValueAt(pos: Int): T = values[(pos - index) % values.size]
}

fun <T> findPattern(values: List<T>): Pattern<T> {
    for ((i, startValue) in values.withIndex()) {
        val length = values.drop(i + 1).indexOf(startValue) + 1
        if (length != 0 && values.subList(i, i + length) == values.subList(i + length, i + (length * 2))) {
            return Pattern(i, values.subList(i, i + length))
        }
    }
    return Pattern(0, emptyList())
}