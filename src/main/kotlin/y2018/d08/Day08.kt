package y2018.d08

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {

    val time = measureTimeMillis {

        val input = File("src/main/resources/y2018/d08/input.txt").readText().split("\\s+".toRegex()).filter { it != "" }.map { it.toInt() }

        val (rootNode) = extractNode(input)
        val result = getNodeAndAllChildren(rootNode)
                .map { it.metadata }
                .flatten()
                .sum()

        println("Part 1: result = $result")
        println("Part 2: result = ${rootNode.value()}")

    }

    println("# $time ms to complete.")
}

fun getNodeAndAllChildren(n: Node): List<Node> {
    return listOf(n) + n.children.flatMap { getNodeAndAllChildren(it) }
}

fun extractNode(input: List<Int>): Pair<Node, List<Int>> {

    val node = Node(input[0], input[1])
    var remainder = input.drop(2)

    for (i in 1..node.childCount) {
       val result = extractNode(remainder)
        node.children.add(result.first)
        remainder = result.second
    }

    node.metadata = remainder.take(node.metaCount)
    remainder = remainder.drop(node.metaCount)

    return Pair(node, remainder)
}

data class Node(val childCount: Int, val metaCount: Int) {

    var metadata: List<Int> = emptyList()
    val children = ArrayList<Node>()

    fun value(): Int {
        return when {
            children.isEmpty() -> metadata.sum()
            else -> metadata.map { if (it <= childCount) children[it - 1].value() else 0 }.sum()
        }
    }
}