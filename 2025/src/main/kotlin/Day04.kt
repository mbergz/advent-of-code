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
        var res = 0
        for ((rowIndex, row) in input.withIndex()) {
            for ((colIndex, col) in row.withIndex()) {
                if (col != '@') continue

                var count = 0
                for ((dx, dy) in directions) {
                    val newX = colIndex + dx
                    val newY = rowIndex + dy

                    if (newY < 0 || newY >= input.size ||
                        newX < 0 || newX >= input[0].length
                    ) continue

                    if (input[newY][newX] == '@') count++
                }

                if (count < 4) res++
            }
        }
        println(res)
    }

}


fun main() = Day04().run()
