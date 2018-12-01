package y2018.d01

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.nio.file.Files
import java.nio.file.Paths

fun getInput() = Files.lines(Paths.get("src/main/resources/y2018/d01/input.txt")).iterator().toObservable().map { it.toInt() }

fun part2(values: Observable<Int>): Int {
        return values
                .cache()
                .repeat()
                .scan(0) { x, y -> x + y }
                .groupBy { it }
                .flatMap { it.skip(1).take(1) }
                .blockingFirst()
}

fun main(args: Array<String>) {
    val part1 = getInput().scan { x, y -> x + y }
    println("Part 1: result = ${part1.blockingLast()}")

    assert(14 == part2(Observable.fromArray(+7, +7, -2, -7, -4)))
    println("Part 2: result = ${part2(getInput())}")
}
