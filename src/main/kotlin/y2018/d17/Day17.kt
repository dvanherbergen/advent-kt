package y2018.d17

import io.reactivex.rxkotlin.toObservable
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

data class Clay(val x: Int, val y: Int)
data class WaterDrop(val x: Int, val y: Int, val state: Char = '|') {
    fun fall() = WaterDrop(x, y + 1)
    fun flowRight() = WaterDrop(x + 1, y)
    fun flowLeft() = WaterDrop(x - 1, y)
    fun stopFlowing() = WaterDrop(x, y, '~')
}

class Grid {

    val data: Array<Array<Char>>
    val minX: Int
    val maxX: Int
    val minY: Int
    val maxY: Int
    var lastRowUsed: Boolean = false

    constructor(clay: List<Clay>, minX: Int, maxX: Int, minY: Int, maxY: Int) {
        data = Array(maxX + 1) { Array(maxY + 1) { '.' } }
        clay.forEach { data[it.x][it.y] = '#' }
        this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this.maxY = maxY
    }

    fun isAvailable(x: Int, y: Int): Boolean {
        return when {
            y > maxY || x > maxX || x < minX -> true
            data[x][y] != '.' -> false
            else -> true
        }
    }

    fun move(from: WaterDrop, to: List<WaterDrop>) {
        data[from.x][from.y] = '.'
        to.forEach {
            if (it.x < maxX + 1 && it.y < maxY + 1) {
                data[it.x][it.y] = '|'
            }
            if (it.y == maxY) {
                lastRowUsed = true
            }
        }
    }

    fun canFlowRightFrom(x: Int, y: Int): Boolean {
        val b = if (y == maxY) '|' else data[x][y + 1]
        return isAvailable(x + 1, y)
                && b != '|' //'~' || b == '#' || b == '.'
    }

    fun canFlowLeftFrom(x: Int, y: Int): Boolean {
        val b = if (y == maxY) '|' else data[x][y + 1]
        return isAvailable(x - 1, y)
                && b != '|' //'~' || b == '#' || b == '.'
    }

    fun stopFlow(drop: WaterDrop): Boolean {

        for (y in drop.y..maxY) {
            if (data[drop.x][y] == '.') {
                return false
            }
            if (data[drop.x][y] == '#') {
                break
            }
            for (x in drop.x - 1 downTo 0) {
                if (data[x][y] == '.') {
                    return false
                }
                if (data[x][y] == '#') {
                    break
                }
            }
            for (x in drop.x + 1..maxX) {
                if (data[x][y] == '.') {
                    return false
                }
                if (data[x][y] == '#') {
                    break
                }
            }
        }
        data[drop.x][drop.y] = '~'
        return true
    }

    fun countDrops(): Long {
        data[500][0] = '+'
        var count = 0L
        for (y in minY..maxY) {
            for (x in 0 until data.size) {
                val c = data[x][y]
                if (c == '|' || c == '~') {
                    count += 1
                }
            }
        }
        return count
    }

    fun countDropsAtRest(): Long {
        data[500][0] = '+'
        var count = 0L
        for (y in minY..maxY) {
            for (x in 0 until data.size) {
                val c = data[x][y]
                if (c == '~') {
                    count += 1
                }
            }
        }
        return count
    }

    fun hasWaterOnLastRow(): Boolean {
        return lastRowUsed
    }
}

fun scanClay(input: String): List<Clay> {

    val (xRange, yRange) = if (input.startsWith("x")) input.split(",") else input.split(",").reversed()

    fun rangeStart(input: String): Int = input.substringAfter("=").substringBefore(".").trim().toInt()
    fun rangeEnd(input: String): Int = input.substringAfter("=").substringAfter("..").trim().toInt()

    val clayBlocks = ArrayList<Clay>()
    for (x in rangeStart(xRange)..rangeEnd(xRange)) {
        for (y in rangeStart(yRange)..rangeEnd(yRange)) {
            clayBlocks.add(Clay(x, y))
        }
    }
    return clayBlocks
}

fun main() {

    val input = File("src/main/resources/y2018/d17/input.txt").readLines()

    val clay = input.map { scanClay(it) }.flatten()

    val minX = clay.minBy { it.x }!!.x - 1
    val maxX = clay.maxBy { it.x }!!.x + 1
    val minY = clay.minBy { it.y }!!.y
    val maxY = clay.maxBy { it.y }!!.y

    val grid = Grid(clay, minX, maxX, minY, maxY)
    val spring = sequence { while (true) yield(WaterDrop(500, 0)) }

    println("Grid ranges from $minX,$minY to $maxX,$maxY")

    val duration = measureTimeMillis {

        val numberOfDrops = spring.iterator().toObservable()
                .scan(emptyList()) { drops: List<WaterDrop>, newDrop: WaterDrop -> flowInGrid(grid, drops + newDrop) }
                .map { if (grid.hasWaterOnLastRow()) grid.countDrops() else 0L }
                .scan(0L) { previousSize, newSize -> if (newSize + previousSize == 0L) newSize else -newSize }
                .filter { it > 0 }

        println("Part 1: result = ${numberOfDrops.blockingFirst()}")
        println("Part 2: result = ${grid.countDropsAtRest()}")

        grid.createImage()
        grid.printToConsole()
    }
    println("Took $duration ms")
}

fun flowInGrid(grid: Grid, drops: List<WaterDrop>): List<WaterDrop> {

    return drops.map {

        val drop = it
        val canFall = grid.isAvailable(drop.x, drop.y + 1)
        val canFlowLeft = grid.canFlowLeftFrom(drop.x, drop.y)
        val canFlowRight = grid.canFlowRightFrom(drop.x, drop.y)

        if (canFall || canFlowLeft || canFlowRight) {
            val updatedDrop = when {
                canFall -> listOf(drop.fall())
                canFlowLeft && canFlowRight -> listOf(drop.flowLeft(), drop.flowRight())
                canFlowLeft -> listOf(drop.flowLeft())
                canFlowRight -> listOf(drop.flowRight())
                else -> listOf(drop)
            }
            grid.move(drop, updatedDrop)
            updatedDrop
        } else {
            if (grid.stopFlow(drop)) {
                listOf(drop.stopFlowing())
            } else {
                listOf(drop)
            }
        }
    }.flatten()
            .filter {
                it.y <= grid.maxY && it.state != '~'
            }
}

fun Grid.createImage() {

    try {
        val width = maxX - minX
        val height = maxY
        val img = BufferedImage(width + 1, height + 1, BufferedImage.TYPE_INT_RGB)
        val f = File("flow.png")
        val colorOfClay = 221 shl 16 or (121 shl 8) or 46
        val colorOfWater = 40 shl 16 or (86 shl 8) or 150
        val colorOfStillWater = 20 shl 16 or (46 shl 8) or 100

        for (x in 0..width) {
            for (y in 0..height) {
                val c = data[x + minX][y]
                if (c == '#') {
                    img.setRGB(x, y, colorOfClay)
                } else if (c == '~') {
                    img.setRGB(x, y, colorOfStillWater)
                } else if (c == '|') {
                    img.setRGB(x, y, colorOfWater)
                }
            }
        }
        ImageIO.write(img, "PNG", f)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Grid.printToConsole() {

    fun printColor(input: Char, color: Int) = print("\u001B[${color}m$input\u001B[0m")
    data[500][0] = '+'

    for (y in 0 until data[0].size) {
        for (x in minX until data.size) {
            val c = data[x][y]
            when (c) {
                '+' -> printColor(c, 36)
                '#' -> print(c)
                '.' -> printColor(c, 35)
                else -> printColor(c, 34)
            }
        }
        println("")
    }
    println("\n\n")
}