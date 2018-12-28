package y2017.d08

import java.io.File

data class Condition(val reg: String, val comparator: String, val value: Int)
data class Instruction(val reg: String, val operation: String, val value: Int, val condition: Condition)

fun main() {

    val instructions = File("src/main/resources/y2017/d08/input.txt").readLines()
            .map { s -> s.split(" ").let { Instruction(it[0], it[1], it[2].toInt(), Condition(it[4], it[5], it[6].toInt())) } }

    val registers = HashMap<String, Int>()

    fun Condition.isValid(): Boolean {
        val a = registers[reg] ?: 0
        return when (comparator) {
            ">" -> a > value
            "<" -> a < value
            "==" -> a == value
            ">=" -> a >= value
            "<=" -> a <= value
            "!=" -> a != value
            else -> throw IllegalArgumentException("unknown condition $comparator")
        }
    }

    fun Instruction.execute(): Int {
        if (condition.isValid()) {
            registers[reg] = (registers[reg] ?: 0) + when (operation) {
                "inc" -> value
                else -> -value
            }
        }
        return registers[reg] ?: 0
    }

    val registerValues = instructions.map { it.execute() }

    println("Part 1: result = ${registers.values.max()}")
    println("Part 2: result = ${registerValues.max()}")
}