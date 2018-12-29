package y2017.d10

fun List<Int>.tieKnot(length: Int, skipSize: Int): List<Int> = (this.take(length).reversed() + this.drop(length)).rotate(length + skipSize)
fun List<Int>.rotate(times: Int): List<Int> = this.drop(times % this.size) + this.take(times % this.size)
fun Int.toHex() = ("0" + this.toString(16)).takeLast(2)

tailrec fun knotHash(values: List<Int>, sequences: List<Int>, skipSize: Int = 0, offset: Int = 0, repeat: Int = 1): List<Int> {

    var skipSize = skipSize
    var values = values
    var offset = offset

    sequences.forEach {
        values = values.tieKnot(it, skipSize)
        offset = (offset + it + skipSize) % values.size
        skipSize += 1
    }

    if (repeat == 1) {
        return values.rotate(values.size - offset)
    } else {
        return knotHash(values, sequences, skipSize, offset, repeat - 1)
    }
}

fun densify(values: List<Int>): List<Int> {
    if (values.isEmpty()) {
        return emptyList()
    }
    return listOf(values.take(16).reduce { a, b -> a xor b }) + densify(values.drop(16))
}

fun denseHash(input: String): String {
    val inputSequence = input.asSequence().map { it.toInt() }.toList() + listOf(17, 31, 73, 47, 23)
    val sparseHash = knotHash((0..255).toList(), inputSequence, repeat = 64)
    return densify(sparseHash).map { it.toHex() }.joinToString("")
}

fun main() {

    val input = "94,84,0,79,2,27,81,1,123,93,218,23,103,255,254,243"
    val values = knotHash((0..255).toList(), input.split(",").map { it.toInt() })
    println("Part 1: result = ${values[0] * values[1]}")

    assert("a2582a3a0e66e6e86e3812dcb672a272" == denseHash(""))
    assert("33efeb34ea91902bb2f59c9920caa6cd" == denseHash("AoC 2017"))
    assert("3efbe78a8d82f29979031a4aa0b16a9d" == denseHash("1,2,3"))
    assert("63960835bcdc130f0b66d7ff4f6a5a8e" == denseHash("1,2,4"))
    println("Part 2: result = ${denseHash(input)}")

}