class Day06 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val nbrs = input.dropLast(1)
            .map { line ->
                line.split("\\s+".toRegex()).map { it.toLong() }
                    .toLongArray()
            }
        val ops = input.last().split("\\s+".toRegex()).joinToString("")

        var res = 0L
        for (i in 0 until nbrs[0].size) {
            var colRes = nbrs[0][i]
            for (j in 1 until nbrs.size) {
                colRes = when (ops[i]) {
                    '+' -> colRes + nbrs[j][i]
                    '*' -> colRes * nbrs[j][i]
                    else -> throw IllegalStateException()
                }
            }
            res += colRes
        }


        println(res)
    }

}


fun main() = Day06().run()
