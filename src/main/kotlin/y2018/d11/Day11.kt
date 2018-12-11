package y2018.d11

data class PowerCell(val x: Int, val y:Int, val serial :Int) {
    fun powerLevel(): Int {
        val rackId = x + 10
        return (((rackId * y) + serial) * rackId).toString().takeLast(3).take(1).toInt() - 5
    }
}

data class Square(val x: Int, val y:Int, val serial: Int) {
    fun totalPower(): Int {
       return listOf(
               PowerCell(x, y, serial),
               PowerCell(x+1, y, serial),
               PowerCell(x+2, y, serial),
               PowerCell(x, y+1, serial),
               PowerCell(x+1, y+1, serial),
               PowerCell(x+2, y+1, serial),
               PowerCell(x, y+2, serial),
               PowerCell(x+1, y+2, serial),
               PowerCell(x+2, y+2, serial)
       ).sumBy { it.powerLevel() }
    }
}

fun main() {


    assert(4 == PowerCell(3,5,8).powerLevel())
    assert(-5 == PowerCell(122,79,57).powerLevel())
    assert(0 == PowerCell(217,196,39).powerLevel())
    assert(4 == PowerCell(101,153,71).powerLevel())


    val squares = sequence {
        for (i in 1..298) {
            for (j in 1..298) {
                yield(Square(i, j, 7989))
            }
        }
    }

    println(squares.maxBy { it.totalPower() })




}