class Day11 : PuzzleRunner() {

    override fun part1(input: List<String>) {
        val mapping: Map<String, List<String>> =
            input.associate { it.substringBefore(":") to it.substringAfter(":").trim().split(" ") }
        println(dfs("you", mapping))
    }

    private fun dfs(current: String, mapping: Map<String, List<String>>): Int {
        if (current == "out") {
            return 1
        }
        var count = 0
        for (c in mapping[current]!!) {
            count += dfs(c, mapping)
        }
        return count
    }

}


fun main() = Day11().run()
