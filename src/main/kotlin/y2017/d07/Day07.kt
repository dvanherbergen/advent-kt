package y2017.d07

import java.nio.file.Files
import java.nio.file.Paths

class Program (
    val name: String,
    var weight: Int = 0,
    var parent: String? = null
)

fun ArrayList<Program>.getProgram(name: String): Program {
    var p = this.find { it.name == name }
    if (p == null) {
        p = Program(name)
        this.add(p)
    }
    return p
}

fun main(args: Array<String>) {

    val programs = ArrayList<Program>()

    Files.readAllLines(Paths.get("src/main/resources/y2017/d07/input.txt"))
            .map { it.filter { c -> c !in charArrayOf(',','(',')', '-', '>') } }
            .map { it.replace("  ", " ") }
            .map { it.split(" ") }
            .forEach {
                println(it)
                val p = programs.getProgram(it[0])
                p.weight = it[1].toInt()
                val children = it.drop(2)
                for (child in children) {
                    val c = programs.getProgram(child)
                    c.parent = p.name
                }
            }


    val root = programs.find { it.parent == null }


    println("Part 1: result = ${root!!.name}")
}