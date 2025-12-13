import kotlin.math.abs
import kotlin.math.max

class Day09 : PuzzleRunner() {

    override fun part1(input: List<String>) {
        var largest = 0L
        for (i in input.indices) {
            val (x1, y1) = input[i].split(",").map { it.toLong() }
            for (j in i + 1 until input.size) {
                val (x2, y2) = input[j].split(",").map { it.toLong() }
                largest = max(largest, (abs(x2 - x1) + 1) * (abs(y2 - y1) + 1))
            }
        }
        println(largest)
    }

}


fun main() = Day09().run()
