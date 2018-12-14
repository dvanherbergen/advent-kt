package y2018.d14

fun getSuperSecretRecipes(whenToStop: (recipes: List<Int>) -> Boolean): String {

    val recipes = mutableListOf(3, 7)
    val elfPositions = intArrayOf(0, 1)

    while (true) {
        elfPositions.map { recipes[it % recipes.size] }.sum().digits().forEach { recipes.add(it) }
        for (i in 0 until elfPositions.size) {
            elfPositions[i] = (elfPositions[i] + 1 + recipes[elfPositions[i]]) % recipes.size
        }
        if (whenToStop(recipes)) {
            return recipes.joinToString("")
        }
    }
}

fun main() {

    val getNext10Recipes = fun(count: Int): String {
        return getSuperSecretRecipes { it.size >= count + 10 }.drop(count).take(10)
    }

    assert("5158916779" == getNext10Recipes(9))
    assert("5941429882" == getNext10Recipes(2018))
    println("Part 1: result = ${getNext10Recipes(990941)}")

    val getRecipesBefore = fun(recipePattern: String): Int {
        return getSuperSecretRecipes { it.takeLast(recipePattern.length).joinToString("") == recipePattern
                    || it.takeLast(recipePattern.length + 1).joinToString("").startsWith(recipePattern) }
                .substringBefore(recipePattern).length
    }

    assert(2018 == getRecipesBefore("59414"))
    assert(10 == getRecipesBefore("15891"))
    println("Part 2: result = ${getRecipesBefore("990941")}")
}

fun Int.digits(): List<Int> {
    return this.toString().asSequence().map { Character.getNumericValue(it) }.toList()
}