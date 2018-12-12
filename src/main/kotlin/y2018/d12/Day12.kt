package y2018.d12

import java.io.File
import kotlin.system.measureTimeMillis

class GrowPattern {

    val plant: Boolean
    val pattern: IntArray

    constructor(input: String) {
        this.plant = input.endsWith("#")
        this.pattern = input.substringBefore(" =>").asSequence()
                .map { when(it) {
                    '#' -> 1
                    else -> 0
                }}.toList().toIntArray()
    }
}

fun main() {

     val input = File("src/main/resources/y2018/d12/input.txt").readLines()

    val start = ".".repeat(5)
    val end = ".".repeat(1000)
    val initialState = (start + input[0].substringAfter(": ") + end).asSequence()
            .map { when(it) {
                '#' -> 1
                else -> 0
            }}.toList().toIntArray()
    val patterns = input.drop(2).map { GrowPattern(it) }.filter { it.plant }


    fun slices(input: IntArray): Sequence<IntArray> {
        return sequence {
            yield(intArrayOf(0,0,0,0,0))
            yield(intArrayOf(0,0,0,0,0))
            for (i in 0..input.size - 6) {
                yield(input.sliceArray(i..i+4))
            }
            yield(intArrayOf(0,0,0,0,0))
            yield(intArrayOf(0,0,0,0,0))
            yield(intArrayOf(0,0,0,0,0))
        }
    }


    println(initialState.size)
    println(initialState.contentToString())

    patterns.forEach { println(it.pattern.contentToString()) }

    println(intArrayOf(0,1,0,1).contentEquals(intArrayOf(0,1,0,1)))

    fun grow(input: IntArray): IntArray {

        return slices(input)
            .map { slice -> if (patterns.find {
                        //println("Comparing ${it.pattern.contentToString()} with ${slice.contentToString()}")
                        it.pattern.contentEquals(slice)
                    } != null) 1 else 0 }
            .toList()
            .toIntArray()
    }

    val time = measureTimeMillis {

    var result = initialState
    //println(" 0: ${result.pretty()}")
    for (i in 1..500) {
        result = grow(result)
//        if (i < 10) {
//            println(" ${i}: ${result.pretty()}")
//        } else {
//            println("${i}: ${result.pretty()}")
//
//        }
    }





    var sum = 0
    for (i in 0..result.size-1) {
        sum += (result[i] * (i- 5))
    }

    println(sum)


    println(result.mapIndexed { i, plant ->
        if (plant == 1) i-5 else 0
    }.filter { it > 0 }
            .map { it + (50000000000 - 500)}
            .sum())

    }
    println("Completed in $time")
}

fun IntArray.pretty(): String {
    return this.asSequence().map { if (it == 1)  "#" else "."}.joinToString(" ")
}