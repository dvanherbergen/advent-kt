package y2017.d06

import java.io.File

fun Array<Int>.nextIndex(i: Int): Int = if (i == this.size - 1) 0 else i + 1

fun Array<Int>.redistribute(): Array<Int> {

    val values = this.clone()
    while (true) {
        val indexOfMax = values.withIndex().maxBy { it.value }!!.index
        val valueToDistribute = values[indexOfMax]
        values[indexOfMax] = 0
        var index = values.nextIndex(indexOfMax)

        for (i in valueToDistribute downTo 1) {
            values[index] += 1
            index = values.nextIndex(index)
        }
        return values
    }
}

fun findLoop(a: Array<Int>): Pair<Int, Int> {

    val combos = ArrayList<String>()
    var count = 0
    var nextArray = a

    do {
        combos.add(nextArray.contentToString())
        count += 1
        nextArray = nextArray.redistribute()
    } while (!combos.contains(nextArray.contentToString()))

    return Pair(count, combos.size - combos.indexOf(nextArray.contentToString()))
}

fun main(args: Array<String>) {

    val input: Array<Int> = File("src/main/resources/y2017/d06/input.txt").readText().split("\\s+".toRegex()).map { it.toInt() }.toTypedArray()

    assert(Pair(5, 4) == findLoop(arrayOf(0, 2, 7, 0)))
    println("Part 1, 2: result = ${findLoop(input)}")
}
