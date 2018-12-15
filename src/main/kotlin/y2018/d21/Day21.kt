package y2018.d21

import y2018.d16.*
import y2018.d19.Instruction
import java.io.File
import kotlin.system.exitProcess

fun main() {

    val input = File("src/main/resources/y2018/d21/input.txt").readLines()

    val ipRegister = input[0].split(" ")[1].toInt()
    val instructions = input.drop(1).map {
        val (operation, a, b, c) = it.split(" ")
        val opFunction = operationsByName[operation] ?: throw IllegalArgumentException(operation)
        Instruction(opFunction, a.toInt(), b.toInt(), c.toInt(), ipRegister)
    }.toTypedArray()

    for ((i, instruction) in instructions.withIndex()) {
        println("$i: ${instruction.pretty()}")
    }

    tailrec fun processInstruction(reg: Registers, ip: Int, opCount: Long): Pair<Registers,Long> {
        if (ip < 0 || ip >= instructions.size) {
            return Pair(reg, opCount)
        }
        val (updatedReg, ip) = instructions[ip].modify(reg, ip)
        return processInstruction(updatedReg, ip, opCount + 1L)
    }

    val potentialMatches = mutableListOf<Int>()

    fun checkAndPrint(reg: Registers, a: Int, b: Int, c: Int): Registers {

        val stopNumber = reg[a]
        if (potentialMatches.contains(stopNumber)) {
            println("Part 2: result = ${potentialMatches.takeLast(1)}")
            exitProcess(0)
        } else {
            if (potentialMatches.isEmpty()) {
                println("Part 1: result = $stopNumber")
            }
            potentialMatches.add(stopNumber)
        }

        return reg.clone().apply {
            this[c] = if (this[a] == this[b]) 1 else 0
        }
    }

    instructions[28] = Instruction(::checkAndPrint, 5, 0, 4, ipRegister)
    processInstruction(intArrayOf(0, 0, 0, 0, 0, 0), 0, 0)
}


fun Instruction.pretty(): String {
    
    var pretty = when (operation) {
        ::addr -> " this[$c] = this[$a] + this[$b] "
        ::addi -> " this[$c] = this[$a] + $b "
        ::mulr -> " this[$c] = this[$a] * this[$b] "
        ::muli -> " this[$c] = this[$a] * $b "
        ::banr -> " this[$c] = this[$a] and this[$b] "
        ::bani -> " this[$c] = this[$a] and $b "
        ::borr -> " this[$c] = this[$a] or this[$b] "
        ::bori -> " this[$c] = this[$a] or $b "
        ::setr -> " this[$c] = this[$a] "
        ::seti -> " this[$c] = $a "
        ::gtir -> " this[$c] = if ($a > this[$b]) 1 else 0 "
        ::gtri -> " this[$c] = if (this[$a] > $b) 1 else 0 "
        ::gtrr -> " this[$c] = if (this[$a] > this[$b]) 1 else 0 "
        ::eqir -> " this[$c] = if ($a == this[$b]) 1 else 0 "
        ::eqri -> " this[$c] = if (this[$a] == $b) 1 else 0 "
        ::eqrr -> " this[$c] = if (this[$a] == this[$b]) 1 else 0 "
        else -> "Unknown OPERATION $operation"
    }

    for (i in 0..5) {
        pretty = pretty.replace("this[$i]", "\u001B[${30+i}mthis[$i]\u001B[0m" )
    }
    return pretty
}