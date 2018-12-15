package y2018.d19

import y2018.d16.Registers
import y2018.d16.operationsByName
import java.io.File

data class Instruction(val operation: ((reg: Registers, a: Int, b: Int, c: Int) -> Registers),
                       val a: Int, val b: Int, val c: Int, val ipRegister: Int) {
    fun modify(reg: Registers, instrPointer: Int): Pair<Registers, Int> {
        reg[ipRegister] = instrPointer
        val updateReg = operation.invoke(reg, a, b, c)
        return Pair(updateReg, updateReg[ipRegister] + 1)
    }
}

fun main() {

    val input = File("src/main/resources/y2018/d19/input.txt").readLines()

    val ipRegister = input[0].split(" ")[1].toInt()
    val instructions = input.drop(1).map {
        val (operation, a, b, c) = it.split(" ")
        val opFunction = operationsByName[operation] ?: throw IllegalArgumentException(operation)
        Instruction(opFunction, a.toInt(), b.toInt(), c.toInt(), ipRegister)
    }.toTypedArray()

    for ((i, instruction) in instructions.withIndex()) {
        println("$i: $instruction")
    }

    fun shortCut(reg: Registers, a: Int, b: Int, c: Int): Registers {
        return reg.clone().apply {
            this[4] = when {
                (this[1] / this[5] > this[4] + 10) -> (this[1] / this[5]) - 1
                (this[1] / this[5] < this[4]) ->  this[1] + 1
                else -> this[4] + 1
            }
        }
    }

    // replace part of the processing loop with an optimized calculation...
    instructions[8] = Instruction(::shortCut, 1, 3, 4, ipRegister)

    tailrec fun processInstruction(reg: Registers, ip: Int): Registers {
        if (ip < 0 || ip >= instructions.size) {
            return reg
        }
        val (updatedReg, ip) = instructions[ip].modify(reg, ip)
        return processInstruction(updatedReg, ip)
    }

    val result1 = processInstruction(intArrayOf(0, 0, 0, 0, 0, 0), 0)
    println("result1 = ${result1[0]}")

    val result2 = processInstruction(intArrayOf(1, 0, 0, 0, 0, 0), 0)
    println("result2 = ${result2[0]}")

}