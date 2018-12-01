package y2017.d05

import java.nio.file.Files
import java.nio.file.Paths

fun calc(values: List<String>, algo: (Int , Int) -> Int): Int {

    val input = values.map { it.toInt() }.toIntArray()
    var index = 0
    var count = 0

    try {
        while (true) {
            val offset = input[index]
            input[index] = algo(offset, input[index])
            index += offset
            count++
        }
    } catch (e : ArrayIndexOutOfBoundsException) {
        return count
    }
}

fun main(args: Array<String>) {

    val jumpUpdate1 = { offset: Int, jump: Int -> jump + 1}
    val jumpUpdate2 = { offset: Int, jump: Int -> if (offset > 2) jump - 1 else jump + 1}

    val testInput = listOf("0", "3", "0", "1", "-3")
    val fileInput = Files.readAllLines(Paths.get("src/main/resources/y2017/d05/input.txt"))

    assert(5 == calc(testInput, jumpUpdate1))
    println("Part 1: result = ${calc(fileInput, jumpUpdate1)}")

    assert(10 == calc(testInput, jumpUpdate2))
    println("Part 2: result = ${calc(fileInput, jumpUpdate2)}")
}
