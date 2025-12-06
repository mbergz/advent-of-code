class Day04 : PuzzleRunner() {
    data class Coord(val x: Int, val y: Int)

    private val directions = listOf(
        Coord(0, -1),
        Coord(1, -1),
        Coord(1, 0),
        Coord(1, 1),
        Coord(0, 1),
        Coord(-1, 1),
        Coord(-1, -1),
        Coord(-1, 0),
    )

    override fun part1(input: List<String>) {
        println(scanPapers(input.map(String::toCharArray)))
    }

    override fun part2(input: List<String>) {
        val grid = input.map(String::toCharArray)
        var total = 0

        while (true) {
            val res = scanPapers(grid)
            total += res
            if (res == 0) break
        }

        println(total)
    }

    private fun scanPapers(input: List<CharArray>): Int {
        val accessible = mutableSetOf<Coord>()

        // Mark accessible
        for ((rowIndex, row) in input.withIndex()) {
            for ((colIndex, col) in row.withIndex()) {
                if (col != '@') continue

                var count = 0
                for ((dx, dy) in directions) {
                    val newX = colIndex + dx
                    val newY = rowIndex + dy

                    if (newY < 0 || newY >= input.size ||
                        newX < 0 || newX >= input[0].size
                    ) continue

                    if (input[newY][newX] == '@') count++
                }

                if (count < 4) {
                    accessible.add(Coord(colIndex, rowIndex))
                }
            }
        }

        // Remove from grid
        for ((rowIndex, row) in input.withIndex()) {
            for ((colIndex, _) in row.withIndex()) {
                if (accessible.contains(Coord(colIndex, rowIndex))) {
                    input[rowIndex][colIndex] = '.'
                }
            }
        }

        return accessible.size
    }
}


fun main() = Day04().run()
