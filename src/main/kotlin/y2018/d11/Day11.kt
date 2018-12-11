package y2018.d11

import kotlin.system.measureTimeMillis

data class PowerCell(val x: Int, val y:Int, val serial :Int) {
    fun powerLevel(): Int {
        val rackId = x + 10
        val r = ((((rackId * y) + serial) * rackId) % 1000)
        return ((r - (r % 100)) / 100) - 5
    }
}


data class Square(val x: Int, val y:Int, val serial: Int, val size: Int = 3) {
    fun totalPower(): Int {

        var power = 0
            for (i in 1..size) {
                for (j in 1..size) {
                   power +=  (PowerCell(x+i-1, y+j-1, serial)).powerLevel()
                }
            }

        return power
    }
}

fun main() {


    assert(4 == PowerCell(3,5,8).powerLevel())
    assert(0 == PowerCell(217,196,39).powerLevel())
    assert(-5 == PowerCell(122,79,57).powerLevel())
    assert(4 == PowerCell(101,153,71).powerLevel())



    val squares = sequence {
        for (i in 1..298) {
            for (j in 1..298) {
                yield(Square(i, j, 7989))
            }
        }
    }

    println(squares.maxBy { it.totalPower() })

    println("Power / ${Square(232, 251, 42, 12).totalPower()}")
    assert(119 == Square(232, 251, 42, 12).totalPower())
    assert(113 == Square(90, 269, 18, 16).totalPower())


    val squares2 = sequence {
        for (i in 1..298) {
            val time = measureTimeMillis {

                for (j in 1..298) {
                    for (s in 1..300) {
                        if ((300 >= i + s) && (300 >= j + s)) {
                            yield(Square(i, j, 7989, s))
                        }
                    }
                }
            }
            println("Checked row: $i in #$time")
        }
    }

    println(squares2.maxBy { it.totalPower() })



}