import java.io.File

data class Star(val x:Int, val y: Int, val vx: Int, val vy: Int) {
    fun move(): Star {
        return Star(x + vx, y + vy, vx, vy)
    }
}

fun main() {

    val regex = """.*<(.*),(.*)>.*<(.*),(.*)>.*""".toRegex()

    var stars = File("src/main/resources/y2018/d10/input.txt").readLines()
            .asSequence()
            .map { regex.matchEntire(it)!!.destructured
                    .let { (a, b, c, d) -> Star(a.trim().toInt(), b.trim().toInt(), c.trim().toInt() ,d.trim().toInt()) }}
            .toList()






   for (i in 1..100000) {

       stars = stars.map { it.move() }

       val minX = stars.minBy { it.x }!!.x
       val maxX = stars.maxBy { it.x }!!.x
       val minY = stars.minBy { it.y }!!.y
       val maxY = stars.maxBy { it.y }!!.y

       if (maxY - minY < 19) {

            println("Seconds $i")
           for (iY in (minY-2)..(maxY+2)  ) {
               for (iX in (minX-2)..(maxX+2)) {
                   val star = stars.find { it.x == iX && it.y == iY }
                   if (star == null) {
                       print(" ")
                   } else {
                       print("#")
                   }
               }
               print("\n")
           }

           break
       }

   }


}