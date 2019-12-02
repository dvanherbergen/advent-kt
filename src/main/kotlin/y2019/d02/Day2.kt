package y2019.d02

fun IntArray.process(index: Int = 0): IntArray {

    when (this[index]) {
        1 -> this[this[index + 3]] = this[this[index + 1]] + this[this[index + 2]]
        2 -> this[this[index + 3]] = this[this[index + 1]] * this[this[index + 2]]
        99 -> return this
        else -> println("oops")
    }

    return this.process(index + 4)
}

fun String.toIntArray() : IntArray {
    return this.split(',').map { it.toInt() }.toIntArray()
}

fun IntArray.update(noun: Int, verb: Int): IntArray {
    this[1] = noun
    this[2] = verb
    return this
}

fun main() {

    val input0 = "1,9,10,3,2,3,11,0,99,30,40,50"
    val input1 = "1,0,0,0,99"
    val input2 = "1,1,1,4,99,5,6,0,99"
    val input3 = "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,1,13,19,1,9,19,23,1,6,23,27,2,27,9,31,2,6,31,35,1,5,35,39,1,10,39,43,1,43,13,47,1,47,9,51,1,51,9,55,1,55,9,59,2,9,59,63,2,9,63,67,1,5,67,71,2,13,71,75,1,6,75,79,1,10,79,83,2,6,83,87,1,87,5,91,1,91,9,95,1,95,10,99,2,9,99,103,1,5,103,107,1,5,107,111,2,111,10,115,1,6,115,119,2,10,119,123,1,6,123,127,1,127,5,131,2,9,131,135,1,5,135,139,1,139,10,143,1,143,2,147,1,147,5,0,99,2,0,14,0"

    assert(input0.toIntArray().process()[0] == 3500)
    assert(input1.toIntArray().process()[0] == 2)
    assert(input2.toIntArray().process()[0] == 30)

    val input = input3.toIntArray().update(12, 2)
    println("Part 1: ${input.process()[0]}") //5305097

    (0..99).forEach { noun ->
        (0..99).forEach { verb ->
            if (input3.toIntArray().update(noun, verb).process()[0] == 19690720) {
                println("Part 2: ${100 * noun + verb}") // 4925
                return
            }
        }
    }
}