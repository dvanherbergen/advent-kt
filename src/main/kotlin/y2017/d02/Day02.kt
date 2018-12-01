package y2017.d02

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.nio.file.Files
import java.nio.file.Paths

fun String.toDigitsObservable() : Observable<Int> = this.split("\\s+".toRegex()).toObservable().map { it.toInt() }

fun part1(rows: Observable<String>) : Maybe<Int> {
    return rows.switchMap {
                val min = it.toDigitsObservable().reduce { x, y -> if (x > y) y else x }.toObservable()
                val max = it.toDigitsObservable().reduce { x, y -> if (x > y) x else y }.toObservable()
                Observable.concat(max, min).reduce { x, y -> x - y }.toObservable()
            }
            .reduce { x, y -> x + y}
}

fun part2(rows: Observable<String>) : Maybe<Int> {
    return rows.switchMap {
                input -> input.toDigitsObservable().switchMap { x -> input.toDigitsObservable().map { y -> Pair(x, y)}  }
            }
            .map { (x, y) -> if (x != y && x % y == 0) x / y else 0 }
            .reduce { x, y -> x + y}
}

fun main(args: Array<String>) {

    val puzzleInput = Files.lines(Paths.get("src/main/resources/y2017/d02/input.txt")).iterator().toObservable().cache()

    assert(18 == part1(listOf("5 1 9 5", "7 5 3", "2 4 6 8").toObservable()).blockingGet())
    println("Part 1: result = ${part1(puzzleInput).blockingGet()}")

    assert(9 == part2(listOf("5 9 2 8", "9 4 7 3", "3 8 6 5").toObservable()).blockingGet())
    println("Part 2: result = ${part2(puzzleInput).blockingGet()}")
}
