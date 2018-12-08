package y2018.d08

import java.io.File

fun main() {


    val input = File("src/main/resources/y2018/d08/input.txt").readText().split("\\s+".toRegex()).filter { it != "" }.map { it.toInt() }


    input.forEach {
        println(it)
    }

    val allNodes: List<Node> = extractNodes(input).first

   allNodes.forEach {
       println(it.metadata)
   }

    val result = allNodes.map { it.metadata }
            .flatten()
            .sum()

    println(result)
}

fun extractNodes(input: List<Int>): Pair<List<Node>, List<Int>> {

    val nodes = ArrayList<Node>()
    var remainder = input

    val n = Node(input[0], input[1])
    nodes.add(n)
    remainder = input.subList(2, input.size)
    for (i in 1..n.childCount) {
        println("loop $i")
        val result = extractNodes(remainder)
        nodes.addAll(result.first)
        remainder = result.second
    }

    n.metadata = remainder.subList(0, n.metaCount)
    remainder = remainder.subList(n.metaCount, remainder.size)

    return Pair(nodes, remainder)
}

class Node(val childCount: Int, val metaCount: Int) {

    var metadata: List<Int> = emptyList()

/*
    val children = List<Node>()

   constructor(values: List<Int>) : this(values[0], values[1]) {

       for (i in childCount) {
           values =
       }

   }

    fun children(): List<Node> {
        return emptyList()
    }
*/
}