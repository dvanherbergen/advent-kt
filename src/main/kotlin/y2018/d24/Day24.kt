package y2018.d24

import java.io.File
import kotlin.math.floor

data class Legion(val id: Int, val units: Int, val hitPoints: Int, val attackDamage: Int, val attackType: String,
                  val initiative: Int, val weaknesses: List<String>, val immunities: List<String>, val army: String, val boost: Int = 0) {
    val effectivePower: Int
        get() = units * (attackDamage + boost)

    val dead: Boolean
        get() = units <= 0

    fun estimateDamage(power: Int, attackType: String): Int {
        return when (attackType) {
            in weaknesses -> power * 2
            in immunities -> 0
            else -> power
        }
    }
    fun absorbDamage(attacker: Legion): Legion {
        val potentialKills = floor(estimateDamage(attacker.effectivePower, attacker.attackType) / hitPoints.toFloat()).toInt()
        val actualKills = if (potentialKills >= units) units else potentialKills
        println("${attacker.army} group ${attacker.id} attacks defending group ${id}, killing ${actualKills} units")
        return this.copy(units = units - actualKills)
    }
}
data class Attack(val attacker: Legion, val target: Legion)

fun List<Legion>.print() {
    val armies = this.partition { it.army == "Immune System" }
    println("\nImmune System:")
    armies.first.filterNot { it.dead }.forEach { println("Group ${it.id} contains ${it.units} units. \u001B[32m$it\u001B[0m") }
    println("\nInfection:")
    armies.second.filterNot { it.dead }.forEach { println("Group ${it.id} contains ${it.units} units. \u001B[33m$it\u001B[0m") }
}

fun buildLegions(name: String, file: String): List<Legion> {

    val input = File(file).readLines()
    val lines = input.drop(input.indexOf("$name:") + 1).takeWhile { it != "" }

    fun units(input: String): Int = input.substringBefore(" ").toInt()
    fun hitPoints(input: String): Int = input.substringBefore(" hit").substringAfterLast(" ").toInt()
    fun attackDamage(input: String): Int = input.substringAfter("does ").substringBefore(" ").toInt()
    fun attackType(input: String): String = input.substringBefore(" damage").substringAfterLast(" ")
    fun initiative(input: String): Int = input.substringAfterLast(" ").toInt()
    fun attributes(input: String, prefix: String): List<String> {
        return if (!input.contains("(")) {
            emptyList()
        } else {
            input.substringAfter("(")
                    .substringBefore(")")
                    .split(";")
                    .filter { it.contains(prefix) }
                    .firstOrNull()
                    ?.substringAfter("to ")
                    ?.split(",")
                    ?.map { it.trim() }
                    ?: emptyList()
        }
    }
    fun weaknesses(input: String): List<String> = attributes(input, "weak to")
    fun immunities(input: String): List<String> = attributes(input, "immune to")
    fun buildLegion(id: Int, s: String): Legion = Legion(id, units(s), hitPoints(s), attackDamage(s), attackType(s), initiative(s), weaknesses(s), immunities(s), name)

    return lines.mapIndexed { i, line -> buildLegion(i + 1, line) }
}

fun List<Legion>.sortByStrongest() = this.sortedWith( compareByDescending<Legion> { it.effectivePower }.thenByDescending { it.initiative } )
fun List<Legion>.findTargetFor(legion: Legion): Legion? {
        return this.filter { it.army != legion.army }
                .filterNot { it.dead }
                .filter { it.estimateDamage(legion.effectivePower, legion.attackType) > 0 }
                .sortedWith( compareByDescending<Legion> { it.estimateDamage(legion.effectivePower, legion.attackType) }.thenByDescending { it.effectivePower }.thenByDescending { it.initiative })
                .firstOrNull()
}
fun List<Legion>.consistsOfTwoArmies(): Boolean = this.filterNot { it.dead }.map { it.army }.groupBy { it }.count() > 1

fun planAttacks(attackers: List<Legion>, targets: List<Legion>): List<Attack> {
    if (attackers.isEmpty()) {
        return emptyList()
    }

    val attacker = attackers.first()
    val target = targets.findTargetFor(attacker)

    return if (target == null) {
        planAttacks(attackers.drop(1), targets)
    } else {
        println("${attacker.army} group ${attacker.id} would deal defending group ${target.id} ${target.estimateDamage(attacker.effectivePower, attacker.attackType)} damage")
        planAttacks(attackers.drop(1), targets.filter { it != target }) + Attack(attacker, target)
    }
}

tailrec fun fight(groups: List<Legion>, rounds: Int = 1): List<Legion> {

    if (rounds > 100_000) {
        // draw...
        return emptyList()
    }
    if (!groups.consistsOfTwoArmies()) {
        return groups.filterNot { it.dead }
    }
    groups.print()
    val attacks = planAttacks(groups.sortByStrongest(), groups)
            .sortedByDescending { it.attacker.initiative }

    val survivors = attacks.fold(groups) { groups, attack ->
        groups.map {
            if (it.army == attack.target.army && it.id == attack.target.id) {
                val attacker = groups.find { attack.attacker.id == it.id && attack.attacker.army == it.army}
                if (attacker == null) it else it.absorbDamage(attacker!!)
            } else {
                it
            }
        }.filterNot { it.dead }
    }
    return fight(survivors, rounds + 1)
}

fun main() {
    val immuneSystem = buildLegions("Immune System", "src/main/resources/y2018/d24/input.txt")
    val infection = buildLegions("Infection", "src/main/resources/y2018/d24/input.txt")

    val remainingUnits = fight(immuneSystem + infection).sumBy { it.units }
    println("Part 1: result = $remainingUnits")

    val remainingUnitsPart2 = (1..10_000).asSequence()
            .map { boost -> fight(infection + immuneSystem.map { it.copy(boost = boost) }) }
            .filter { it.isNotEmpty() }
            .filter { it[0].army == "Immune System" }
            .first()
            .sumBy { it.units }
    println("Part 2: result = $remainingUnitsPart2") // 15429 -> too high, 3099 -> too high
}