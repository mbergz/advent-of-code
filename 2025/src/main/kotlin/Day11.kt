class Day11 : PuzzleRunner() {

    override fun part1(input: List<String>) {
        val mapping: Map<String, List<String>> =
            input.associate { it.substringBefore(":") to it.substringAfter(":").trim().split(" ") }
        println(dfsPart1("you", mapping))
    }

    private fun dfsPart1(current: String, mapping: Map<String, List<String>>): Int {
        if (current == "out") {
            return 1
        }
        var count = 0
        for (c in mapping[current]!!) {
            count += dfsPart1(c, mapping)
        }
        return count
    }

    override fun part2(input: List<String>) {
        val mapping: Map<String, List<String>> =
            input.associate { it.substringBefore(":") to it.substringAfter(":").trim().split(" ") }
        println(dfsPart2("svr", mapping).bothPaths)
    }

    data class NodeInfo(val emptyPaths: Long, val dacPaths: Long, val fftPaths: Long, val bothPaths: Long)

    private fun dfsPart2(
        current: String,
        mapping: Map<String, List<String>>,
        memo: HashMap<String, NodeInfo> = HashMap()
    ): NodeInfo {
        val memoized = memo[current]
        if (memoized != null) {
            return memoized
        }

        if (current == "out") {
            return NodeInfo(1, 0, 0, 0)
        }

        var empty = 0L
        var dac = 0L
        var fft = 0L
        var both = 0L

        for (next in mapping[current]!!) {
            val sub = dfsPart2(next, mapping, memo)

            when (next) {
                "dac" -> {
                    both += sub.fftPaths
                    dac += sub.emptyPaths
                }

                "fft" -> {
                    both += sub.dacPaths
                    fft += sub.emptyPaths
                }

                else -> {
                    dac += sub.dacPaths
                    fft += sub.fftPaths
                    empty += sub.emptyPaths
                    both += sub.bothPaths
                }
            }

        }

        val nodeInfo = NodeInfo(empty, dac, fft, both)
        memo[current] = nodeInfo
        return nodeInfo
    }

}


fun main() = Day11().run()
