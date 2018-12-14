package y2018.d14

fun main() {


    val input = mutableListOf<Int>(3,7)
//    val input = mutableListOf<Int>(9,9,0,9,4,1)
    val elfPositions = mutableListOf<Int>(0, 1)
            //    val elfPositions = mutableListOf<Int>(0, 1, 2, 3, 4, 5)

    val start = 990941

    println(input)
    while (true) {


        var score = 0
        elfPositions.forEach {
            score += input[it % input.size]
        }


        score.toString().forEach {
            input.add(("" + it).toInt())
        }



      //  println("Position ${elfPositions[0]} + to move (${(1 +  input[elfPositions[0]])}) = ${((elfPositions[0] + 1 +  input[elfPositions[0]]) % input.size)}")
        for (i in 0 until elfPositions.size) {
            elfPositions[i] = ((elfPositions[i] + 1 +  input[elfPositions[i]]) % input.size)

        }

//        for (i in 0 until input.size) {
//            if (i == elfPositions[0]) {
//                print("(${input[i]}) ")
//            } else if (i == elfPositions[1]) {
//                print("[${input[i]}] ")
//            } else {
//                print(" ${input[i]}  ")
//            }
//        }
//        println("")

       if (input.size >= start + 10) {
           break;
       }
    }




    println(input)
    // 3230474240

    println(input.slice(start until start+10).joinToString(""))


    while (true) {


        var score = 0
        elfPositions.forEach {
            score += input[it % input.size]
        }


        score.toString().forEach {
            input.add(("" + it).toInt())
        }



        //  println("Position ${elfPositions[0]} + to move (${(1 +  input[elfPositions[0]])}) = ${((elfPositions[0] + 1 +  input[elfPositions[0]]) % input.size)}")
        for (i in 0 until elfPositions.size) {
            elfPositions[i] = ((elfPositions[i] + 1 +  input[elfPositions[i]]) % input.size)

        }


        if ((input[input.size - 1]) == 1) {
            if (input.takeLast(6).joinToString("") == "990941") {
                println("took ${input.size - 6}")
                break;
            }
        }
    }
//153723222

}