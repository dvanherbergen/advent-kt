package y2017.d01

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import java.nio.file.Files
import java.nio.file.Paths

fun String.toSingleDigitsObservable(): Observable<Int> = this.chars().iterator().toObservable().cache().map { Character.getNumericValue(it) }

class Day01 {

    fun part1(input: String): Maybe<Int> {
        return (input + input[0]).chars().iterator().toObservable()
                .map { Character.getNumericValue(it) }
                .scan(Pair(0, false)) { p, v -> Pair(v, v == p.first) }
                .map { if (it.second) it.first else 0 }
                .reduce { x, y -> x + y }
    }

    fun part2(input: String): Maybe<Int> {
        return input.toSingleDigitsObservable()
                .zipWith(input.toSingleDigitsObservable().repeat(2).skip(input.length / 2L))
                .filter { it.first == it.second }
                .map { it.first }
                .reduce { x, y -> x + y }
    }
}

fun main(args: Array<String>) {
    val input = Files.readString(Paths.get("src/main/resources/y2017/d01/input.txt"))
    println("Part 1: result = ${Day01().part1(input).blockingGet()}")

    assert(12 == Day01().part2("123123").blockingGet())
    println("Part 2: result = ${Day01().part2(input).blockingGet()}")
}