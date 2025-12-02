import kotlin.math.abs

class Day01 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        var dial = 50

        val count = input.count {
            dial = Math.floorMod(dial + parse(it), 100)
            dial == 0
        }

        println(count)
    }

    override fun part2(input: List<String>) {
        var dial = 50
        var count = 0

        input.forEach {
            val oldDial = dial
            dial += parse(it)

            if (dial >= 100) {
                count += dial / 100
            }
            if (dial < 0) {
                count += abs(Math.floorDiv(dial, 100))
                if (oldDial == 0) count--
                if (Math.floorMod(dial, 100) == 0) count++
            }
            if (dial == 0) {
                count++
            }

            dial = Math.floorMod(dial, 100)
        }

        println(count)
    }

    private fun parse(input: String): Int {
        val op = if (input[0] == 'L') -1 else 1
        return input.drop(1).toInt() * op
    }
}

fun main() = Day01().run()
