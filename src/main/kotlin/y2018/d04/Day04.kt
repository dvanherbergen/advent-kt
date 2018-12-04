package y2018.d04

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

data class SleepClock(val guard: Int, val hour: Int, val minute: Int) {
    private fun tick(): SleepClock {
        return when (minute) {
            59 -> SleepClock(guard, hour + 1, 0)
            else -> SleepClock(guard, hour, minute + 1)
        }
    }

    private fun after(c: SleepClock): Boolean {
        return (hour > c.hour || hour == c.hour && minute > c.minute)
    }

    fun tickTo(c: SleepClock): List<SleepClock> {
        val ticks: MutableList<SleepClock> = ArrayList()
        var time = this
        while (c.after(time)) {
            ticks.add(time)
            time = time.tick()
        }
        return ticks
    }
}

fun main(args: Array<String>) {

    var currentGuard = 0
    var lastSleepStart = SleepClock(0, 0, 0)

    val sleepMinutes = File("src/main/resources/y2018/d04/input.txt").readLines()
            .sortedBy { it.substringBefore("]") }
            .map {
                val time = it.substring(12).substringBefore("]").split(":")
                if (it.contains("#")) {
                    currentGuard = it.substringAfter("#").substringBefore(" ").toInt()
                    Collections.emptyList<SleepClock>()
                } else {
                    val sc = SleepClock(currentGuard, time[0].toInt(), time[1].toInt())
                    if (it.contains("asleep")) {
                        lastSleepStart = sc
                        Collections.emptyList<SleepClock>()
                    } else {
                        lastSleepStart.tickTo(sc)
                    }
                }
            }
            .flatten()
            .filter { it.hour == 0 }


    val guardWithMostSleepMinutes = sleepMinutes
            .groupingBy { it.guard }
            .eachCount()
            .maxBy { it.value }?.key

    val maxMinutesForGuard = sleepMinutes
            .filter { it.guard == guardWithMostSleepMinutes }
            .groupingBy { it.minute }
            .eachCount()
            .maxBy { it.value }?.key

    println("Part 1: result = ${maxMinutesForGuard!! * guardWithMostSleepMinutes!!}")

    val maxMinutesByGuard = sleepMinutes
            .groupingBy { Pair(it.minute, it.guard) }
            .eachCount()
            .maxBy { it.value }?.key

    println("Part 1: result = ${maxMinutesByGuard!!.first * maxMinutesByGuard!!.second}")
}