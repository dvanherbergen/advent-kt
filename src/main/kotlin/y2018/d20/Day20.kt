package y2018.d20

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import java.io.File
import java.util.*

data class Door(val x: Int, val y: Int) {
    fun move(direction: Char): Door {
        return when (direction) {
            'N' -> Door(x, y - 1)
            'E' -> Door(x + 1, y)
            'S' -> Door(x, y + 1)
            else -> Door(x - 1, y)
        }
    }
}

val demo1 = "^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$"
val demo2 = "^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$"
val demo3 = "^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$"

fun main() {

    val input = File("src/main/resources/y2018/d20/input.txt").readText()

    val position = ArrayDeque<Door>()
    val graph = SimpleGraph<Door, DefaultEdge>(DefaultEdge::class.java)
    var currentPos = Door(0, 0)
    graph.addVertex(currentPos)

    input.asSequence().forEach {
        currentPos = when (it) {
            '$', '^' -> currentPos
            '(' -> {
                position.push(currentPos)
                currentPos
            }
            ')' -> position.pop()
            '|' -> position.peek()
            else -> {
                val newPos = currentPos.move(it)
                graph.addVertex(newPos)
                graph.addEdge(currentPos, newPos)
                newPos
            }
        }
    }

    val distances = graph.vertexSet().map {
       DijkstraShortestPath(graph).getPath(Door(0, 0), it).vertexList
    }

    val maxDistance = distances.maxBy { it.size }!!.size - 1
    println("Part 1: result = $maxDistance}")

    val roomsFarAway = distances.filter { it.size > 1000 }.count()
    println("Part 2: result = $roomsFarAway")
}

