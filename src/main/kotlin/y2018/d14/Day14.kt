package y2018.d14

fun main() {


    val input = mutableListOf<Int>(3,7)
    val elfPositions = mutableListOf<Int>(0, 1)
    var recipesCreated = 0

    println(input)
    while (true) {


        var score = 0
        elfPositions.forEach {
            score += input[it % input.size]
        }

        if (score > 9) {
            val score1 = score / 10
            input.add(score1)
           // recipesCreated ++
        }

        val score2 = score % 10
        input.add(score2)
        recipesCreated ++

      //  println("Position ${elfPositions[0]} + to move (${(1 +  input[elfPositions[0]])}) = ${((elfPositions[0] + 1 +  input[elfPositions[0]]) % input.size)}")
        for (i in 0 until elfPositions.size) {
            elfPositions[i] = ((elfPositions[i] + 1 +  input[elfPositions[i]]) % input.size)

        }

       // println(input)
       // println("Positions aft $elfPositions")
      //  println("")
        for (i in 0 until input.size) {
            if (i == elfPositions[0]) {
                print("(${input[i]}) ")
            } else if (i == elfPositions[1]) {
                print("[${input[i]}] ")
            } else {
                print(" ${input[i]}  ")
            }
        }
        println("")

       if (input.size >= 2018 + 10) {
           break;
       }
    }

    println(input.takeLast(10).joinToString(""))

}