package y2018.d22

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultUndirectedWeightedGraph
import org.jgrapht.graph.DefaultWeightedEdge

data class Region(val geoIndex: Int, val erosionLevel: Int, val type: Char) {
    fun compatibleToolTypes(): List<Char> {
        return when (type) {
            '|' -> listOf('T', 'N')
            '=' -> listOf('C', 'N')
            '.' -> listOf('C', 'T')
            else -> listOf('T')
        }
    }

    val riskLevel: Int
        get() = when (type) {
            '|' -> 2
            '=' -> 1
            else -> 0
        }
}

data class CaveLocation(val x: Int, val y: Int, val tool: Char)

typealias CaveSystem = Array<Array<Region?>>

val depth = 3339
val targetX = 10
val targetY = 715
val padding = 20

fun detectType(erosionLevel: Int, x: Int, y: Int): Char {
    return when {
        x == 0 && y == 0 -> 'M'
        x == targetX && y == targetY -> 'T'
        erosionLevel % 3 == 0 -> '.'
        erosionLevel % 3 == 1 -> '='
        erosionLevel % 3 == 2 -> '|'
        else -> '?'
    }
}

fun detectErosionLevel(geoIndex: Int): Int = (depth + geoIndex) % 20183

fun buildRegion(x: Int, y: Int, caveSystem: CaveSystem): Region {
    val geoIndex = if (x == targetX && y == targetY) 0 else caveSystem[y][x - 1]!!.erosionLevel * caveSystem[y - 1][x]!!.erosionLevel
    val erosionLevel = detectErosionLevel(geoIndex)
    val type = detectType(erosionLevel, x, y)
    return Region(geoIndex, erosionLevel, type)
}

fun costOfMoving(from: CaveLocation, to: CaveLocation): Double {
    return when {
        from.tool == to.tool -> 1.0
        from.x == to.x && from.y == to.y -> 7.0
        else -> 8.0
    }
}

fun main() {

    val caveSystem = CaveSystem(targetY + padding) { y ->
        Array(targetX + padding) { x ->
            val geoIndex = when {
                x == 0 && y == 0 -> 0
                y == 0 -> x * 16807
                x == 0 -> y * 48271
                else -> -1
            }
            val erosionLevel = detectErosionLevel(geoIndex)
            val type = detectType(erosionLevel, x, y)
            if (geoIndex == -1) null else Region(geoIndex, erosionLevel, type)
        }
    }

    caveSystem.withIndex().forEach { (y, row) ->
        row.withIndex().forEach { (x, region) ->
            if (region == null) {
                caveSystem[y][x] = buildRegion(x, y, caveSystem)
            }
        }
    }

    val riskLevel = caveSystem.withIndex()
            .filter { it.index <= targetY }
            .map { (y, row) ->
                row.withIndex()
                        .filter { it.index <= targetX }
                        .map { (x, region) -> region?.riskLevel ?: 0 }
                        .sum()
            }.sum()
    println("Part 1: result = $riskLevel")

    val mouth = CaveLocation(0, 0, 'T')
    val target = CaveLocation(targetX, targetY, 'T')
    val graph = DefaultUndirectedWeightedGraph<CaveLocation, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    val caveSystemLocations = caveSystem.mapIndexed { y, row ->
        row.mapIndexed { x, region -> region!!.compatibleToolTypes().map { CaveLocation(x, y, it) } }
    }

    // add vertexes
    caveSystemLocations.forEach { it.forEach { locations -> locations.forEach { loc -> graph.addVertex(loc) } } }

    fun addEdge(from: CaveLocation, to: CaveLocation) {
        val edge = DefaultWeightedEdge()
        graph.addEdge(from, to, edge)
        graph.setEdgeWeight(edge, costOfMoving(from, to))
    }

    // add edges
    caveSystemLocations.withIndex().forEach { (y, row) ->
        row.withIndex().forEach { (x, locations) ->
            locations.forEach { from ->
                if (x + 1 < caveSystem[0].size && y + 1 < caveSystem.size) {
                    // cost of moving right
                    caveSystemLocations[y][x + 1].forEach { to -> addEdge(from, to) }
                    // cost of moving down
                    caveSystemLocations[y + 1][x].forEach { to -> addEdge(from, to) }
                }
            }
            if (locations.size == 2) {
                // cost of changing tools on the spot
                addEdge(locations[0], locations[1])
            }
        }
    }

    val caveRoute = DijkstraShortestPath(graph).getPath(mouth, target).vertexList
    val numberOfMoves = caveRoute.size - 1
    val numberOfToolSwitches = caveRoute.fold(Pair(mouth, 0)) { acc, to ->
        if (acc.first.tool != to.tool) {
            Pair(to, acc.second + 1)
        } else acc
    }.second

    caveSystem.print(caveRoute)
    println("Part 2: result = $numberOfMoves moves, $numberOfToolSwitches tool switches. Total time: ${numberOfMoves + (numberOfToolSwitches * 7)}")
}

private fun CaveSystem.print(shortestPath: List<CaveLocation> = emptyList()) {

    fun printColor(input: Char, color: Int, bgColor: Int = 0) = print("\u001B[${color};${bgColor}m$input\u001B[0m")

    this.withIndex().forEach { (y, row) ->
        row.withIndex().forEach { (x, region) ->
            val color = when (region?.type) {
                'M', 'T' -> 91
                '|' -> 32
                '.' -> 37
                '=' -> 34
                else -> 36
            }
            if (region != null) {
                if (shortestPath.find { it.x == x && it.y == y } != null) {
                    printColor(region.type, 31)
                } else {
                    printColor(region.type, color)
                }
            } else {
                print(" ")
            }
        }
        println("")
    }
}

