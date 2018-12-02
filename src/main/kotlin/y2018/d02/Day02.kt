package y2018.d02

import java.nio.file.Files
import java.nio.file.Paths

fun getInput() = Files.readAllLines(Paths.get("src/main/resources/y2018/d02/input.txt"))

fun String.twoAndThreeCount(): Pair<Int, Int> {
    return this.asSequence()
            .groupingBy { it }
            .eachCount()
            .values
            .fold(Pair(0, 0)) { acc, v ->
                when (v) {
                    2 -> Pair(1, acc.second)
                    3 -> Pair(acc.first, 1)
                    else -> acc
                }
            }
}

fun String.differsByOne(other: String): Boolean {
    return 1 == this.zip(other)
            .asSequence()
            .filter { it.first != it.second }
            .count()
}

fun String.commonLetters(other: String): String {
    return this.asIterable().intersect(other.asIterable()).joinToString("")
}

fun part1(input: List<String>): Int {
    val counts = input
            .asSequence()
            .map { it.twoAndThreeCount() }
            .reduce { x, y -> Pair(x.first + y.first, x.second + y.second) }
    return counts.first * counts.second
}

fun part2(codes: List<String>): String {
    for (e in codes) {
        for (f in codes) {
            if (e.differsByOne(f)) {
                return e.commonLetters(f)
            }
        }
    }
    return ""
}

fun main(args: Array<String>) {
    assert(Pair(1,1) == "bababc".twoAndThreeCount())
    assert(Pair(0,1) == "abcccd".twoAndThreeCount())
    assert(12 == part1(listOf("abcdef", "bababc", "abbcde", "abcccd", "aabcdd", "abcdee", "ababab")))
    println("Part 1: result = ${part1(getInput())}")

    assert(!"abcde".differsByOne("axcye"))
    assert("fghij".differsByOne("fguij"))
    assert("fgij" == "fghij".commonLetters("fguij"))
    println("Part 2: result = ${part2(getInput())}")
}