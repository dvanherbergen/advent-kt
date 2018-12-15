package y2018.d15

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import java.io.File
import kotlin.system.measureTimeMillis

data class Location(val x: Int, val y: Int) {
    fun above() = Location(x, y - 1)
    fun right() = Location(x + 1, y)
    fun below() = Location(x, y + 1)
    fun left() = Location(x - 1, y)
}

data class Creature(val location: Location, val type: Char, val hitPoints: Int, val attackPower: Int) {
    fun isDead() = hitPoints <= 0
    fun id() = "$type(${location.x},${location.y},$hitPoints)"
}

data class Cave(val rows: List<String>)

fun Creature.attack(victim: Creature?): Creature? {
    return if (victim == null || isDead() || victim.isDead()) {
        victim
    } else {
        Creature(victim.location, victim.type, victim.hitPoints - this.attackPower, victim.attackPower)
    }
}

fun Creature.print() {
    print("$type($hitPoints, $attackPower)  ")
}

fun List<Creature>.hitPoints(): Int {
    return this.map { it.hitPoints }.sum()
}

fun List<Creature>.inReadingOrder(): List<Creature> {
    return this.sortedWith(compareBy<Creature> { it.location.y }.thenBy { it.location.x })
}

fun List<Creature>.findEnemiesFor(creature: Creature): List<Creature> {
    return this.filter { it.location != creature.location && it.type != creature.type }
}

fun List<Creature>.thatAreAlive(): List<Creature> {
    return this.filter { !it.isDead() }
}

fun List<Creature>.findAdjacentTarget(creature: Creature): Creature? {
    val nearByEnemies = findEnemiesFor(creature)
            .filter { it.location in listOf(creature.location.above(), creature.location.right(), creature.location.below(), creature.location.left()) }
    val lowestHitPoints = nearByEnemies.minBy { it.hitPoints }?.hitPoints ?: 0
    return nearByEnemies.inReadingOrder().find { it.hitPoints == lowestHitPoints }
}

fun List<Creature>.isSingleSpecies(): Boolean {
    return this.groupBy { it.type }.keys.size == 1
}

fun List<Creature>.update(creature: Creature): List<Creature> {
    return this.map { if (it.location == creature.location) creature else it }.filter { !it.isDead() }
}

fun Location.isUnoccupiedSquare(cave: Cave, creatures: List<Creature>): Boolean {
    return when {
        this.x < 0 || this.y < 0 -> false
        creatures.find { it.location == this && !it.isDead() } != null -> false
        cave.rows[y][x] == '.' -> true
        else -> false
    }
}

fun Cave.getOpenSpace(): List<Location> {
    return rows.mapIndexed { y, row -> row
                .asSequence()
                .mapIndexed { x, c -> Pair(x, c) }
                .filter { it.second == '.' }
                .map { Location(it.first, y) }
                .toList()
    }.flatten()
}

fun findPossibleFirstSteps(cave: Cave, allCreatures: List<Creature>, location: Location): List<Location> {
    return listOf(location.above(), location.below(), location.left(), location.right())
            .filter { it.isUnoccupiedSquare(cave, allCreatures) }
}

fun findSquaresInRange(cave: Cave, enemies: List<Creature>, allCreatures: List<Creature>): List<Location> {
    return enemies.map { listOf(it.location.above(), it.location.below(), it.location.left(), it.location.right()) }
            .flatten()
            .filter { it.isUnoccupiedSquare(cave, allCreatures) }
}

fun getShortestPath(cave: Cave, creatures: List<Creature>, from: Location, to: Location): List<Location> {

    val graph = SimpleGraph<Location, DefaultEdge>(DefaultEdge::class.java)
    val vertexes = cave.getOpenSpace().filter { it.isUnoccupiedSquare(cave, creatures) } + from + to
    vertexes.forEach {
        graph.addVertex(it)
    }

    fun addEdges(loc: Location) {
        if (vertexes.contains(loc.above())) graph.addEdge(loc, loc.above())
        if (vertexes.contains(loc.right())) graph.addEdge(loc, loc.right())
        if (vertexes.contains(loc.below())) graph.addEdge(loc, loc.below())
        if (vertexes.contains(loc.left())) graph.addEdge(loc, loc.left())
    }

    addEdges(to)
    vertexes.forEach { addEdges(it) }
    addEdges(from)
    return DijkstraShortestPath(graph).getPath(from, to)?.vertexList ?: emptyList()
}

fun areAllElvesAlive(allCreatures: List<Creature>, remainingCreatures: List<Creature>): Boolean {
    return remainingCreatures.filter { !it.isDead() && it.type == 'E' }.size == allCreatures.filter { it.type == 'E' }.size
}

tailrec fun battle(cave: Cave, creatures: List<Creature>, completedRounds: Int): Pair<Int, List<Creature>> {

    println("\nAfter $completedRounds completed rounds: ")
    printCombatArea(cave, creatures)
    println("Starting round ${completedRounds + 1}")

    var creaturesThatMustFight = creatures.inReadingOrder()
    var creaturesWithPTSS = emptyList<Creature>()

    fun allCreatures() = creaturesThatMustFight + creaturesWithPTSS

    while (creaturesThatMustFight.thatAreAlive().isNotEmpty()) {

        if (allCreatures().thatAreAlive().isSingleSpecies()) {
            println("No more enemies..")
            return Pair(completedRounds, allCreatures().thatAreAlive())
        }

        var soldier = creaturesThatMustFight.thatAreAlive().first()
        if (soldier.isDead()) {
            creaturesThatMustFight = creaturesThatMustFight.drop(1)
            continue
        }

        var attackableTarget = allCreatures().thatAreAlive().findAdjacentTarget(soldier)
        if (attackableTarget == null) {

            val enemies = allCreatures().thatAreAlive().findEnemiesFor(soldier)
            if (enemies.isNotEmpty()) {

                val targetSquares = findSquaresInRange(cave, enemies, allCreatures().thatAreAlive())
                if (targetSquares.isNotEmpty()) {

                    val firstSteps = findPossibleFirstSteps(cave, allCreatures().thatAreAlive(), soldier.location)
                    println("Moving ${soldier.id()} to one of $firstSteps")
                    val firstStepsWithDistance = firstSteps.map { step ->
                        if (targetSquares.contains(step)) {
                            Pair(step, 0)
                        } else {
                            val shortestDistance = targetSquares
                                    .map { getShortestPath(cave, allCreatures().thatAreAlive(), step, it) }
                                    .filter { it.isNotEmpty() }
                                    .minBy { it.size }?.size
                            Pair(step, shortestDistance ?: -1)
                        }
                    }.filter { it.second != -1 }
                            .sortedWith(compareBy<Pair<Location, Int>> { it.second }.thenBy { it.first.y }.thenBy { it.first.x })
                    if (firstStepsWithDistance.isNotEmpty()) {
                        val firstStep = firstStepsWithDistance.first().first
                        println("Chosen step : $firstStep")
                        soldier = Creature(firstStep, soldier.type, soldier.hitPoints, soldier.attackPower)
                        attackableTarget = allCreatures().thatAreAlive().findAdjacentTarget(soldier)
                    }
                }
            }
        }

        if (attackableTarget != null) {
            val attackedTarget = soldier.attack(attackableTarget)
            println("${soldier.id()} attacked ${attackableTarget.id()} -> ${attackedTarget!!.id()}")
            if (attackedTarget != null) {
                creaturesThatMustFight = creaturesThatMustFight.update(attackedTarget)
                creaturesWithPTSS = creaturesWithPTSS.update(attackedTarget)
            }
        }
        creaturesThatMustFight = creaturesThatMustFight.drop(1)
        creaturesWithPTSS += soldier
    }

    return battle(cave, allCreatures().thatAreAlive(), completedRounds + 1)
}

fun main() {

    val duration = measureTimeMillis {

        assert(27730 == process("demo2.txt").first)
        assert(27755 == process("demo5.txt").first)
        assert(36334 == process("demo3.txt").first)
        assert(39514 == process("demo4.txt").first)
        assert(28944 == process("demo6.txt").first)
        assert(18740 == process("demo7.txt").first)

        println("Part 1 result = ${process("input.txt").first}")

        for (power in 4..100) {
            val result = process("input.txt", power)
            if (result.second) {
                println("Part 2 result = We need elf power level $power. Combat result: ${result.first}")
                break
            }
        }
    }
    println("Took $duration ms.")
}

fun process(file: String, attackPower: Int = 3): Pair<Int, Boolean> {

    val input = File("src/main/resources/y2018/d15/$file").readLines()
    val cave = Cave(input.map { it.replace('G', '.').replace('E', '.') })
    val creatures = input.mapIndexed { y, row ->
        row
                .asSequence()
                .mapIndexed { x, c -> Pair(x, c) }
                .filter { it.second in listOf('E', 'G') }
                .map { Creature(Location(it.first, y), it.second, 200, if (it.second == 'E') attackPower else 3) }
                .toList()
    }.flatten()

    val (rounds, remainingCreatures) = battle(cave, creatures, 0)
    return Pair(rounds * remainingCreatures.hitPoints(), areAllElvesAlive(creatures, remainingCreatures))
}

fun printCombatArea(cave: Cave, creatures: List<Creature>, extras: List<Location> = emptyList()) {

    println("\u001B[35mCreatures: ${creatures} \u001B[0m")

    for ((y, row) in cave.rows.withIndex()) {
        for ((x, char) in row.withIndex()) {
            val creature = creatures.find { it.location == Location(x, y) }
            val extra = extras.findLast { it == Location(x, y) }
            when {
                extra != null -> print('?')
                creature != null -> print(creature.type)
                else -> print(char)
            }
            print(' ')
        }
        print("   ")
        creatures.filter { it.location.y == y }
                .sortedWith(compareBy { it.location.x })
                .forEach { it.print() }
        println()
    }
    println()
}


