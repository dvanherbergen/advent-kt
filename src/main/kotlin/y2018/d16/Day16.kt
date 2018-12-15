package y2018.d16

import io.reactivex.rxkotlin.toObservable
import java.io.File

typealias Registers = IntArray
typealias Instruction = IntArray

data class Sample(val before: Registers, val instruction: Instruction, val after: Registers)
data class OpCodeMatch(val opCode: Int, val operations: List<String>)

fun addr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] + this[b] }
fun addi(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] + b }
fun mulr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] * this[b] }
fun muli(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] * b }
fun banr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] and this[b] }
fun bani(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] and b }
fun borr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] or this[b] }
fun bori(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] or b }
fun setr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = this[a] }
fun seti(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = a }
fun gtir(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = if (a > this[b]) 1 else 0 }
fun gtri(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = if (this[a] > b) 1 else 0 }
fun gtrr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = if (this[a] > this[b]) 1 else 0 }
fun eqir(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = if (a == this[b]) 1 else 0 }
fun eqri(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = if (this[a] == b) 1 else 0 }
fun eqrr(r: Registers, a: Int, b: Int, c: Int): Registers = r.clone().apply { this[c] = if (this[a] == this[b]) 1 else 0 }

val operations = listOf(::addr, ::addi, ::mulr, ::muli, ::banr, ::bani, ::borr, ::bori, ::setr, ::seti, ::gtir, ::gtri, ::gtrr, ::eqir, ::eqri, ::eqrr)
val operationsByName = operations.associateBy { it.name }

fun main() {

    val samples = File("src/main/resources/y2018/d16/input_1.txt").readLines().toObservable()
            .filter { it.isNotBlank() }
            .buffer(3)
            .map {
                val before = it[0].substringAfter("[").substringBefore("]").split(",").map { it.trim().toInt() }.toIntArray()
                val instr = it[1].split(" ").map { it.trim().toInt() }.toIntArray()
                val after = it[2].substringAfter("[").substringBefore("]").split(",").map { it.trim().toInt() }.toIntArray()
                Sample(before, instr, after)
            }
            .toList()
            .blockingGet()

    val result = samples.map { it.findMatchingOpCodes().size }.count { it >= 3 }
    println("Part 1: result = $result")

    val instructions = File("src/main/resources/y2018/d16/input_2.txt").readLines()
            .map { it.split(" ").map { it.trim().toInt() }.toIntArray() }

    var opCodes = samples.map { sample -> OpCodeMatch(sample.instruction[0], sample.findMatchingOpCodes()) }
            .groupBy { it.opCode }
            .map {
                it.key to it.value.map { it.operations }.reduce { acc, list -> acc.intersect(list).toList() }
            }.sortedBy { it.second.size }

    opCodes.forEach {
        val matches: List<String> = opCodes.map { it.second }.filter { it.size == 1 }.map { it[0] }
        opCodes = opCodes.map {
            if (it.second.size > 1) {
                it.first to it.second.filter { matches.indexOf(it) == -1 }
            } else {
                it
            }
        }.sortedBy { it.first }
    }

    fun findOperationName(opCode: Int): String = opCodes.find { it.first == opCode }!!.second[0]

    val part2 = instructions.fold(intArrayOf(0, 0, 0, 0)) { register, instruction ->
        val (opCode, a, b, c) = instruction
        operationsByName[findOperationName(opCode)]!!.invoke(register, a, b, c)
    }
    println("Part 2: result = ${part2[0]}")
}

fun Sample.findMatchingOpCodes(): List<String> {
    val (_, a, b, c) = this.instruction
    return operations.map {
        if (it.invoke(before, a, b, c).contentEquals(after)) {
            it.name
        } else { "" }
    }.filter { it != "" }
}

