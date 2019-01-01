package y2017.d12

import java.io.File

fun main() {

    val relations = mutableMapOf<String, List<String>>()
    val input = File("src/main/resources/y2017/d12/input.txt").readLines()

    input.forEach {
        val (id, links) = it.split(" <-> ")
        relations.put(id, links.split(", "))
    }

    fun findConnectedPrograms(name: String, allReadyFound: List<String> = emptyList()): List<String> {
        if (allReadyFound.contains(name)) {
            return allReadyFound
        }
        var all = allReadyFound + name
        relations[name]?.forEach {
            all = findConnectedPrograms(it, all)
        }
        return all.distinct()
    }

    val connectedPrograms = findConnectedPrograms("0")
    println("Part 1: result = ${connectedPrograms.size}")


    var programsToCheck = relations.keys
    var groupCount = 0

    while (programsToCheck.isNotEmpty()) {
        groupCount++
        programsToCheck.removeAll(findConnectedPrograms(programsToCheck.first()))
    }

    println("Part 2: result = $groupCount")
}