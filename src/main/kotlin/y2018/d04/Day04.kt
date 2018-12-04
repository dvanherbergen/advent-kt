package y2018.d04

import java.io.File


data class Clock(val hour: Int, val minute: Int) {
    fun tick(): Clock {
        return when (minute) {
            59 -> Clock(hour+1, 0)
            else -> Clock( hour, minute + 1)
        }
    }
    fun midnightHour(): Boolean {
        return hour == 0
    }

}


fun main(args: Array<String>) {

    var currentGuard = 0;
    var lastSleepStart: Clock = Clock(0,0)

    val demo = File("src/main/resources/y2018/d04/demo.txt").readLines()
                .sortedBy { it.substringBefore("]") }

    .forEach {
        println(it)
        val time = it.substring(12).substringBefore("]").split(":")
        val currentTime = Clock(time.get(0).toInt(), time.get(1).toInt())
        println(currentTime)

       if (it.contains("#")) {
            currentGuard = it.substringAfter("#").substringBefore(" ").toInt()
       } else {
            if (it.contains("asleep")) {
                println("$currentGuard : sleep")
                lastSleepStart = currentTime
            } else {
                println("$currentGuard : up")

                while (currentTime > lastSleepStart) {

                }

                // calculate sleep minutes
                if (lastSleepStart.midnightHour()) {

                } else {

                }
            }
       }
    }

    println(demo)

}