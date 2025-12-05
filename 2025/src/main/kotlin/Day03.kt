class Day03 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val res = input.map {
            var highest = 0
            var firstIdx = -1
            for (i in 0 until it.length - 1) {
                if (it[i].digitToInt() > highest) {
                    highest = it[i].digitToInt()
                    firstIdx = i
                }
            }
            highest = 0
            for (i in (firstIdx + 1)..<it.length) {
                if (it[i].digitToInt() > highest) {
                    highest = it[i].digitToInt()
                }
            }
            (it[firstIdx].toString() + highest).toInt()
        }.reduce { acc, nbr -> acc + nbr }

        println(res)
    }


}

fun main() = Day03().run()
