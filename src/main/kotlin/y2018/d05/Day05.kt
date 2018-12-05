package y2018.d05

import java.io.File

fun main(args: Array<String>) {

    val polymer = File("src/main/resources/y2018/d05/input.txt").readText()
    println("Part 1: result = ${polymer.getReducedSize()}")

    val allPolymerSizes = ('a'..'z').asSequence().map { c ->
        polymer.filter { it != c && it != c.toUpperCase() }.getReducedSize()
    }
    println("Part 2: result = ${allPolymerSizes.min()}")
}

fun String.getReducedSize(): Int {
    return this.asSequence()
            .fold("") { polymer, unit -> polymer.reactWith(unit) }
            .trim()
            .length
}

fun String.reactWith(c: Char): String {
    return when {
        this.isEmpty() -> c.toString()
        this.last().isOpposite(c) -> this.dropLast(1)
        else -> this + c
    }
}

fun Char.isOpposite(c: Char): Boolean {
    return c != this && (c.toUpperCase() == this || c.toLowerCase() == this)
}
