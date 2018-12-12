package y2018.d12

import io.reactivex.rxkotlin.toObservable
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {

    val input = File("src/main/resources/y2018/d12/input.txt").readLines()
    val offset = 5
    val initialPots = ".".repeat(offset) + input[0].substringAfter(": ") + ".".repeat(200)
    val growPatterns = input.drop(2).filter { it.endsWith("#") }.map { it.substringBefore(" =>") }

    fun groupPots(pots: String): Sequence<String> {
        return sequence {
            for (i in 0..pots.length - 5) {
                yield(pots.substring(i, i + 5))
            }
        }
    }

    fun grow(pots: String): String {
        return groupPots(pots)
                .map { slice -> if (growPatterns.find { it == slice } != null) '#' else '.' }
                .joinToString("", prefix = pots.take(2), postfix = pots.takeLast(2))
    }

    val result = (1..20).fold(initialPots) { pots, i -> grow(pots) }
    println("Part 1: result = ${totalPotValue(-offset.toLong(), result)}")

    val duration = measureTimeMillis {
        val result2 = (1..50_000_000_000).toObservable()
                .scan(Pair(50_000_000_000 - offset, initialPots)) { p, _ -> Pair(p.first - 1, grow(p.second)) }
                .skipWhile { it.second.rotate() != grow(it.second) }
                .blockingFirst()
        println("Part 2: result = ${totalPotValue(result2.first, result2.second)}")
    }

    println("Took $duration ms to complete.")
}

fun totalPotValue(offset: Long, pots: String): Long {
    return pots.asSequence()
            .mapIndexed { i, pot -> if (pot == '#') offset + i else 0 }
            .sum()
}

fun String.rotate(): String {
    return this.takeLast(1) + this.dropLast(1)
}