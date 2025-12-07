class Day06 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val nbrs = input.dropLast(1)
            .map { it.trim() }
            .map { line ->
                line.split("\\s+".toRegex()).map { it.toLong() }
                    .toLongArray()
            }
        val ops = input.last().split("\\s+".toRegex()).joinToString("")

        var res = 0L
        for (i in 0 until nbrs[0].size) {
            var colRes = nbrs[0][i]
            for (j in 1 until nbrs.size) {
                if (ops[i] == '+') colRes += nbrs[j][i]
                if (ops[i] == '*') colRes *= nbrs[j][i]
            }
            res += colRes
        }

        println(res)
    }

    override fun part2(input: List<String>) {
        val colNbrs = mutableListOf<String>()
        var res = 0L

        var colIdx = input[0].length - 1

        while (colIdx >= 0) {
            val sb = StringBuilder()
            for (rowIdx in 0..input.size - 2) {
                if (input[rowIdx][colIdx] != ' ') sb.append(input[rowIdx][colIdx])
            }
            colNbrs.add(sb.toString())

            if (input[input.size - 1][colIdx] != ' ') {
                if (input[input.size - 1][colIdx] == '+') {
                    res += colNbrs.map { it.toLong() }.reduce { acc, nbr -> acc + nbr }
                }
                if (input[input.size - 1][colIdx] == '*') {
                    res += colNbrs.map { it.toLong() }.reduce { acc, nbr -> acc * nbr }
                }
                colNbrs.clear()
                colIdx--
            }

            colIdx--
        }
        println(res)
    }
}


fun main() = Day06().run()
