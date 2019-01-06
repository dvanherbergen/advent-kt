package y2017.d13

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

interface Scanner {
    fun isAtStartOnSecond(delay: Int): Boolean
}

class NoOpScanner : Scanner {
    override fun isAtStartOnSecond(delay: Int): Boolean = false
}

class FirewallScanner(val range: Int) : Scanner {
    override fun isAtStartOnSecond(delay: Int): Boolean = delay % (((range - 2) * 2) + 2) == 0
}

fun buildFirewall(): Array<Scanner> {
    val input = File("src/main/resources/y2017/d13/input.txt").readLines()
    val layers = input.map { it.substringBefore(":").toInt() }.max()?.plus(1) ?: 1
    val firewall = Array<Scanner>(layers) { NoOpScanner() }
    input.forEach {
        val (i, range) = it.split(": ").map { v -> v.toInt() }
        firewall[i] = FirewallScanner(range)
    }
    return firewall
}

fun Array<Scanner>.severity(): Int {
    return this.withIndex()
            .map { (i, scanner) ->
                if (scanner is FirewallScanner) {
                    if (scanner.isAtStartOnSecond(i)) i * scanner.range else 0
                } else {
                    0
                }
            }
            .sum()
}

fun Array<Scanner>.isSafe(delay: Int = 0): Boolean {
    this.withIndex().forEach { (i, scanner) ->
        if (scanner is FirewallScanner && scanner.isAtStartOnSecond(i + delay)) {
            return false
        }
    }
    return true
}

fun main() {

    val firewall = buildFirewall()
    println("Part 1: result = ${firewall.severity()}")

    val routines = (0..3).map {
        GlobalScope.async {
            for (delay in it..100_000_000 step 4) {
                if (firewall.isSafe(delay)) {
                    println("Part 2: result = $delay")
                    exitProcess(0)
                }
            }
        }
    }

    runBlocking { routines.map { it.await() } }
}