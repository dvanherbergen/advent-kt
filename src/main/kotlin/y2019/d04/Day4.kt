package y2019.d04

fun main() {

    val part1Selection = (347312..805915).asSequence()
            .map { it.toString() }
            .filter { it[0] == it[1]
                    || it[1] == it[2]
                    || it[2] == it[3]
                    || it[3] == it[4]
                    || it[4] == it[5]
            }
            .filter { it[0] <= it[1]
                    && it[1] <= it[2]
                    && it[2] <= it[3]
                    && it[3] <= it[4]
                    && it[4] <= it[5]
            }

    println("Part 1: ${part1Selection.count()}")

    val part2Selection = part1Selection.filter {
        Regex("(.)\\1*")
                .findAll(it)
                .map { it.value.length }
                .filter { it == 2}
                .count() > 0
    }
    println("Part 2: ${part2Selection.count()}")
}
