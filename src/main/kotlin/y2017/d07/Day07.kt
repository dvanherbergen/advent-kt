package y2017.d07

import java.io.File

class Program ( val name: String, val weight: Int, val childNames: List<String>)


fun main() {

    val programs = File("src/main/resources/y2017/d07/input.txt").readLines().map {
        val name = it.substringBefore(" ")
        val weight = it.substringAfter("(").substringBefore(")").toInt()
        val childNames = if (it.contains("->")) it.substringAfter("-> ").split(", ") else emptyList()
        Program(name, weight, childNames)
    }

    fun Program.getChildren(): List<Program> = programs.filter { it.name in this.childNames }
    fun Program.getTotalWeight(): Int = this.getChildren().map { it.getTotalWeight() }.sum() + this.weight
    fun Program.isBalanced(): Boolean = this.childNames.isEmpty() || this.getChildren().map { it.getTotalWeight() }.distinct().size == 1

    val root = programs.first { pgm -> programs.find { it.childNames.contains(pgm.name) } == null }

    println("Part 1: root program = ${root.name}")

    fun findUnbalancedProgram(program: Program): Pair<Program, Int> {
        program.getChildren().forEach {
            if (!it.isBalanced()) {
                return findUnbalancedProgram(it)
            }
        }
        val childrenByWeight = program.getChildren().groupBy { it.getTotalWeight() }
        val unbalancedProgram = childrenByWeight.values.first { it.size == 1 }.first()
        val expectedWeight = childrenByWeight.values.first { it.size > 1 }.first().getTotalWeight()
        return Pair(unbalancedProgram, expectedWeight)
    }

    val (unbalancedProgram, requiredWeight) = findUnbalancedProgram(root)
    val expectedWeight = requiredWeight - unbalancedProgram.getTotalWeight() + unbalancedProgram.weight
    println("Required weight for unbalanced disk: ${unbalancedProgram.name} = $expectedWeight")
}