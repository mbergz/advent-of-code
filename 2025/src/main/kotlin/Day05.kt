class Day05 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val (ranges, ids) = input.filter(String::isNotBlank).partition { it.contains("-") }
        val res = ids.filter { id ->
            ranges.any { r ->
                val (start, end) = r.split("-").map { it.toLong() }
                id.toLong() in start..end
            }
        }.toList().count()

        println(res)
    }

}


fun main() = Day05().run()
