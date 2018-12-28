package y2017.d09

import java.io.File
import java.util.*

data class State(val depth: Int = 0, val ignore: Boolean = false, val ignoreNext: Boolean = false, val garbage: Boolean = false, val c: Char)

fun calcScore(input: String): Pair<Int, Int> {

    val buffer = ArrayDeque<Char>()
    var score = 0
    var depth = 0
    var garbage = 0

    input.asSequence().forEach { c ->
        val previous = buffer.peek()
        if (previous == '!') {
            buffer.pop()
        } else if (previous == '<' && c == '>') {
            buffer.pop()
        } else if (previous != '<' || c == '!') {
            buffer.push(c)
            if (c == '{') {
                depth += 1
                score += depth
            } else if (c == '}') {
                depth -= 1
            }
        } else {
            garbage += 1
        }
    }
    return Pair(score, garbage)
}

fun main() {

    assert(3 == calcScore("{{<a!>},{<a!>},{<a!>},{<ab>}}").first)
    assert(9 == calcScore("{{<!!>},{<!!>},{<!!>},{<!!>}}").first)
    assert(10 == calcScore("<{o\"i!a,<{i<a>").second)
    assert(0 == calcScore("<!!!>>").second)

    val result = calcScore(File("src/main/resources/y2017/d09/input.txt").readText())
    println("Part 1: result = ${result.first}")
    println("Part 2: result = ${result.second}")
}