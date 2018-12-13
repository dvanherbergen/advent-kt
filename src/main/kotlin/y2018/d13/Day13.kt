package y2018.d13

import io.reactivex.rxkotlin.toObservable
import java.io.File

enum class Direction {
    Left, Right, Up, Down, Straight;

    fun turnLeft(): Direction {
        return when (this) {
            Left -> Down
            Down -> Right
            Right -> Up
            else -> Left
        }
    }

    fun turnRight(): Direction {
        return when (this) {
            Left -> Up
            Up -> Right
            Right -> Down
            else -> Left
        }
    }
}

data class Cart(val x: Int, val y: Int, val direction: Direction, val nextTurn: Direction = Direction.Left) {

    infix fun move(getStreet: (x: Int, y: Int) -> Char): Cart {

        val (nextX, nextY) = when (direction) {
            Direction.Down -> Pair(x, y + 1)
            Direction.Left -> Pair(x - 1, y)
            Direction.Up -> Pair(x, y - 1)
            else -> Pair(x + 1, y)
        }

        val nextDirection = when (getStreet(nextX, nextY)) {
            '/' -> when (direction) {
                Direction.Left, Direction.Right -> direction.turnLeft()
                else -> direction.turnRight()
            }
            '\\' -> when (direction) {
                Direction.Left, Direction.Right -> direction.turnRight()
                else -> direction.turnLeft()
            }
            '+' -> when (nextTurn) {
                Direction.Left -> direction.turnLeft()
                Direction.Right -> direction.turnRight()
                else -> direction
            }
            else -> direction
        }

        val nextTurnDirection = when (getStreet(nextX, nextY)) {
            '+' -> when (nextTurn) {
                Direction.Left -> Direction.Straight
                Direction.Straight -> Direction.Right
                else -> Direction.Left
            }
            else -> nextTurn
        }
        return Cart(nextX, nextY, nextDirection, nextTurnDirection)
    }
}


fun main() {

    val input = File("src/main/resources/y2018/d13/input.txt").readLines()

    val carts = input.mapIndexed { y, row ->
        row.withIndex().asSequence()
                .filter { it.value in listOf('>', 'v', '<', '^') }
                .map {
                    val direction = when (it.value) {
                        'v' -> Direction.Down
                        '<' -> Direction.Left
                        '^' -> Direction.Up
                        else -> Direction.Right
                    }
                    Cart(it.index, y, direction)
                }.toList()
    }.flatten()

    val grid = input.map {
        it.replace('>', '-').replace('>', '-').replace('v', '|').replace('^', '|')
    }

    fun onStreet(x: Int, y: Int): Char {
        return grid[y][x]
    }

    fun sortCarts(carts: List<Cart>): List<Cart> {
        return carts.sortedWith(compareBy<Cart> { it.y }.thenBy { it.x })
    }

    fun cleanupCrashes(carts: List<Cart>, cartToMove: Int): Pair<List<Cart>, Int> {
        val crashSite = carts.asSequence()
                .groupingBy { Pair(it.x, it.y) }
                .eachCount()
                .filter { it.value == 2 }
                .keys
                .firstOrNull()
        val cleanedCarts = carts.fold(Pair<List<Cart>, Int>(ArrayList(), cartToMove)) { acc, cart ->
            if (crashSite == null || !(cart.x == crashSite.first && cart.y == crashSite.second)) {
                Pair(acc.first.plus(cart), acc.second)
            } else {
                Pair(acc.first, if (acc.second > acc.first.size) acc.second - 1 else acc.second)
            }
        }
        return if (cleanedCarts.second > cleanedCarts.first.size - 1) {
            Pair(cleanedCarts.first, cleanedCarts.first.size - 1)
        } else {
            cleanedCarts
        }
    }

    fun moveCart(cartsState: Pair<List<Cart>, Int>, cleanup: (x: List<Cart>, y: Int) -> Pair<List<Cart>, Int>): Pair<List<Cart>, Int> {
        val (carts, cartToMove) = cartsState
        val nextCars = when {
            carts.size == 1 -> listOf(carts[0] move ::onStreet)
            else -> carts
                    .take(cartToMove)
                    .plus(carts[cartToMove] move ::onStreet)
                    .plus(carts.drop(cartToMove + 1))
        }
        val nextCartToMove = when {
            cartToMove + 1 >= carts.size -> 0
            else -> cartToMove + 1
        }
        val (cleanedCarts, cleanedToMove) = cleanup(nextCars, nextCartToMove)
        return if (cleanedToMove == 0) {
            Pair(sortCarts(cleanedCarts), 0)
        } else {
            Pair(cleanedCarts, cleanedToMove)
        }
    }

    val firstCrash = (1..10000).toObservable()
            .scan(Pair(carts, 0)) { x, _ -> moveCart(x) { a, b -> Pair(a, b) } }
            .map { acc ->
                acc.first
                        .asSequence()
                        .groupingBy { Pair(it.x, it.y) }
                        .eachCount()
                        .filter { it.value > 1 }
            }.filter { it.isNotEmpty() }

    println("First crash site: ${firstCrash.blockingFirst()}")

    val c1 = listOf(Cart(124, 47, Direction.Down, Direction.Straight), Cart(77, 133, Direction.Up, Direction.Straight), Cart(77, 133, Direction.Down, Direction.Straight))
    val c2 = listOf(Cart(77, 133, Direction.Up, Direction.Straight), Cart(124, 47, Direction.Down, Direction.Straight), Cart(77, 133, Direction.Down, Direction.Straight))
    val c3 = listOf(Cart(77, 133, Direction.Up, Direction.Straight), Cart(77, 133, Direction.Down, Direction.Straight), Cart(124, 47, Direction.Down, Direction.Straight))
    val d1 = listOf(Cart(1234, 47, Direction.Down, Direction.Straight), Cart(124, 47, Direction.Down, Direction.Straight), Cart(77, 133, Direction.Up, Direction.Straight), Cart(77, 133, Direction.Down, Direction.Straight))
    val d2 = listOf(Cart(1234, 47, Direction.Down, Direction.Straight), Cart(77, 133, Direction.Up, Direction.Straight), Cart(124, 47, Direction.Down, Direction.Straight), Cart(77, 133, Direction.Down, Direction.Straight))
    val d3 = listOf(Cart(1234, 47, Direction.Down, Direction.Straight), Cart(77, 133, Direction.Up, Direction.Straight), Cart(77, 133, Direction.Down, Direction.Straight), Cart(124, 47, Direction.Down, Direction.Straight))

    assert(0 == cleanupCrashes(c1, 2).second)
    assert(0 == cleanupCrashes(c1, 1).second)
    assert(0 == cleanupCrashes(c1, 0).second)
    assert(0 == cleanupCrashes(c2, 2).second)
    assert(0 == cleanupCrashes(c3, 2).second)
    assert(1 == cleanupCrashes(d1, 3).second)
    assert(1 == cleanupCrashes(d2, 2).second)
    assert(1 == cleanupCrashes(d3, 2).second)

    val lastCart = (1..100000).toObservable()
            .scan(Pair(carts, 0)) { x, _ -> moveCart(x, ::cleanupCrashes) }
            .filter { it.first.size == 1 && it.second == 0 }
            .map { it.first.first() }

    println("Last cart: ${lastCart.blockingFirst()}")
}


