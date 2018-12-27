package y2018.d02

import java.io.File

fun getInput() = File("src/main/resources/y2018/d02/input.txt").readLines()

fun String.hasLetterCount(n: Int): Boolean {
    return this.asSequence()
            .groupingBy { it }
            .eachCount()
            .containsValue(n)
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
    return input.filter { it.hasLetterCount(2) }.count() *
            input.filter { it.hasLetterCount(3) }.count()
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
    assert("bababc".hasLetterCount(2))
    assert("bababc".hasLetterCount(3))
    assert(12 == part1(listOf("abcdef", "bababc", "abbcde", "abcccd", "aabcdd", "abcdee", "ababab")))
    println("Part 1: result = ${part1(getInput())}")

    assert(!"abcde".differsByOne("axcye"))
    assert("fghij".differsByOne("fguij"))
    assert("fgij" == "fghij".commonLetters("fguij"))
    println("Part 2: result = ${part2(getInput())}")
}