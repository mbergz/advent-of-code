class Day07 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val grid = input.map { it.toCharArray() }

        var splits = 0

        for (rowIdx in 1 until grid.size - 1) {
            val row = grid[rowIdx]
            for ((colIdx, curr) in row.withIndex()) {
                val above = grid[rowIdx - 1][colIdx]
                if (above != 'S' && above != '|') continue

                if (curr == '^') {
                    splits++
                    grid[rowIdx][colIdx - 1] = '|'
                    grid[rowIdx][colIdx + 1] = '|'
                } else {
                    grid[rowIdx][colIdx] = '|'
                }

            }
        }
        println(splits)
    }

}


fun main() = Day07().run()
