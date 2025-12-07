class Day07 : PuzzleRunner() {
    data class Coord(val x: Int, val y: Int)

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

    override fun part2(input: List<String>) {
        val grid = input.map { it.toCharArray() }

        val start = Coord(grid[0].indexOf('S'), 0)
        val timelines = dfs(start, grid)

        println(timelines)
    }

    private fun dfs(coord: Coord, grid: List<CharArray>, memo: HashMap<Coord, Long> = HashMap()): Long {
        if (coord.y == grid.size - 1) return 1

        if (memo.contains(coord)) {
            return memo.getValue(coord)
        }

        var paths = 0L

        if (grid[coord.y][coord.x] == '^') {
            paths += dfs(Coord(coord.x - 1, coord.y), grid, memo)
            paths += dfs(Coord(coord.x + 1, coord.y), grid, memo)
        } else {
            paths += dfs(Coord(coord.x, coord.y + 1), grid, memo)
        }

        memo[coord] = paths
        return paths
    }

}


fun main() = Day07().run()
