import org.junit.jupiter.api.Test
import y2017.d01.Day01

class Day1Test {

    @Test
    fun test() {

        Day01().part1("1122").test().assertResult(3)
        Day01().part1("1221").test().assertResult(3)
        Day01().part1("1111").test().assertResult(4)
        Day01().part1("1234").test().assertResult(0)
        Day01().part1("91212129").test().assertResult(9)

    }
}