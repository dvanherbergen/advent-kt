package y2017.d03
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

fun calcSteps(t: Pair<Long, Long>): Long {
    return t.first.absoluteValue + t.second.absoluteValue
}

fun part1(value: Long): Long {
    val x = Observable.interval(0, TimeUnit.MICROSECONDS)
            .filter { it % 2 != 0L}
            .takeWhile { x -> value > (x * x) || value in ((x*x)..((x+2)*(x+2)))}
            .scan( Triple(0L, 0L, 0L)) { x, y -> Triple(x.first + 1, x.second - 1, y) }
            .takeLast(1)
            .map { Triple(it.first, it.second, (it.third + 2)) }
            .blockingFirst()

    val band = x.third
    var current = band * band
    var c = Pair(x.first, x.second)

    if (current == value) {
        return calcSteps(c)
    } else {

        for (i in 2..band) {
            c = Pair(c.first-1, c.second)
            if (--current == value) return calcSteps(c)
        }
        for (i in 2..band) {
            c = Pair(c.first+1 , c.second)
            if (--current == value) return calcSteps(c)
        }
        for (i in 2..band) {
            c = Pair(c.first, c.second+1)
            if (--current == value) return calcSteps(c)
        }
        for (i in 2..band) {
            c = Pair(c.first-1 , c.second)
            if (--current == value) return calcSteps(c)
        }

    }
    return 0
}


fun main(args: Array<String>) {
    assert(4L == part1(13))
    assert(4L == part1(17))
    assert(31L == part1(1024))
    println("Part 1: result = ${part1(265149)}")
}
