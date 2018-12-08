package y2018.d07

import java.io.File

fun main() {

    val instructions = File("src/main/resources/y2018/d07/input.txt").readLines()
            .asSequence()
            .map { Pair(it.substring(5,6), it.substring(36,37)) }
            .sortedBy { it.first }

    fun String.prerequisiteSteps(): List<String> {
        return instructions.filter { it.second == this }.map { it.first }.toList()
    }

    fun String.containsAllPrerequisitesFor(c: String): Boolean {
        return c.prerequisiteSteps().count { p -> !this.contains(p) } == 0
    }

    fun String.getSuccessiveSteps(): List<String> {
        return instructions.filter { it.first == this }
                .map { it.second }
                .sortedBy { it }
                .toList()
    }

    var result = ""
    var backlog = instructions.map { it.first }
                    .filter { it.prerequisiteSteps().isEmpty() }
                    .distinct()
                    .sorted()
                    .toList()

    while (backlog.isNotEmpty()) {
        val nextStep = backlog.firstOrNull { result.containsAllPrerequisitesFor(it) } ?: ""
        result += nextStep
        backlog = nextStep.getSuccessiveSteps().filter { !backlog.contains(it) } + backlog
        backlog = backlog.sorted().filter { it != nextStep }
    }

    println("Part 1: result = $result")

    var completedSteps = ""
    var unfinishedSteps = result.asSequence().map{ it.toString() }.toList()

    fun getNextAvailableStep(): String {
        val nextStep = unfinishedSteps.firstOrNull { completedSteps.containsAllPrerequisitesFor(it) } ?: ""
        unfinishedSteps = unfinishedSteps.filter { it != nextStep }
        return nextStep
    }

    val workers = IntRange(1, 5).map { Worker(it) }.toList()

    for (second in 0..1000) {
        workers.forEach {
            completedSteps += it.getCompletedStep(second)
        }
        workers.filter { it.isAvailableOn(second) }.forEach {
            it.startNewStep(second, getNextAvailableStep())
        }
        if (workers.count { !it.isAvailableOn(second) } == 0) {
            println("Part 2: result = $second")
            break
        }
    }
}

class Worker(val id: Int) {

    var currentStep = ""
    var endSecond = -1

    fun getCompletedStep(second: Int): String {
        return when {
            endSecond == second -> currentStep
            else -> ""
        }
    }
    fun startNewStep(second: Int, step: String) {
        currentStep = step
        endSecond = second + step.stepDuration()
    }
    fun isAvailableOn(second: Int): Boolean {
        return second >= endSecond
    }
}

fun String.stepDuration(): Int {
    return if (this == "") 0 else this[0] - 'A' + 61
}