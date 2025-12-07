import kotlin.math.max

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

    override fun part2(input: List<String>) {
        val ranges = input
            .filter { it.contains("-") }
            .map { Pair(it.split("-")[0].toLong(), it.split("-")[1].toLong()) }
            .toList()
            .sortedBy { it.first }

        val mergedRanges = mutableListOf<Pair<Long, Long>>()

        var i = 0
        while (i < ranges.size) {
            val start = ranges[i].first
            var end = ranges[i].second

            while (i < ranges.size - 1 && ranges[i + 1].first <= end) {
                end = max(end, ranges[i + 1].second)
                i++
            }

            mergedRanges.add(Pair(start, end))
            i++
        }

        val res = mergedRanges.fold(0L) { acc, pair -> acc + ((pair.second - pair.first) + 1) }
        println(res)
    }
}


fun main() = Day05().run()
