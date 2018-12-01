package y2017.d03

data class Coordinate (val x: Int, val y: Int) {
    fun topLeft() = Coordinate(x + 1, y - 1)
    fun top() = Coordinate(x + 1, y)
    fun topRight() = Coordinate(x + 1, y + 1)
    fun right() = Coordinate(x, y + 1)
    fun bottomRight() = Coordinate(x - 1, y + 1)
    fun bottom() = Coordinate(x - 1, y)
    fun bottomLeft() = Coordinate(x - 1, y - 1)
    fun left() = Coordinate(x, y - 1)
}

enum class Direction {
    Left, Right, Up, Down
}

class Grid {

    private val values = HashMap<Coordinate, Int>()
    private var direction = Direction.Down

    private fun getValue(c: Coordinate): Int {
        return values.getOrDefault(c, 0)
    }

    private fun addCoordinate(c: Coordinate): Int {
        val pointValue = getValue(c.top()) + getValue(c.left()) + getValue(c.bottom()) + getValue(c.right()) + getValue(c.topLeft()) + getValue(c.topRight()) + getValue(c.bottomLeft()) + getValue(c.bottomRight())
        values[c] = pointValue
        return pointValue
    }

    fun build(max: Int): Int {
        var c = Coordinate(0, 0)
        values[c] = 1
        var v = 0
        while (v < max) {
            c = getNextAvailablePos(c)
            v = addCoordinate(c)
        }
        return v
    }

    private fun getNextAvailablePos(c: Coordinate): Coordinate {

        fun chooseNextCoordinate(nextDir: Direction, nextCoordinateNewDir: Coordinate, nextCoordinateSameDir: Coordinate): Coordinate {
            return if (!values.containsKey(nextCoordinateNewDir)) {
                direction = nextDir
                nextCoordinateNewDir
            } else {
                nextCoordinateSameDir
            }
        }

        return when (direction) {
            Direction.Right -> chooseNextCoordinate(Direction.Up, c.top(), c.right())
            Direction.Up -> chooseNextCoordinate(Direction.Left, c.left(), c.top())
            Direction.Left -> chooseNextCoordinate(Direction.Down, c.bottom(), c.left())
            Direction.Down -> chooseNextCoordinate(Direction.Right, c.right(), c.bottom())
        }
    }
}



fun main(args: Array<String>) {
    println("Part 2: result = ${Grid().build(265149)}")
}
