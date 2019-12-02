package y2019.d01

import java.io.File

fun calcFuel(mass: Int) : Int {
    val fuel = ((mass / 3) - 2)
    return if (fuel > 0) {
        fuel + calcFuel(fuel)
    } else {
        0
    }
}

fun main() {

    val input = File("src/main/resources/y2019/d01/input.txt").readLines()
            .map { it.toInt() }

    val fuel = input
            .map { (it / 3) - 2 }
            .sum()

    println("Part 1: result = $fuel") // 3160932

    val totalFuel = input
            .map { calcFuel(it) }
            .sum()

    println("Part 2: result = $totalFuel") // 4738549
}





