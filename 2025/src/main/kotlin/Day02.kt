class Day02 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val res = mutableListOf<Long>()

        for (range in input.first().split(",")) {
            val (from, to) = range.split("-")
            for (i in from.toLong()..to.toLong()) {
                val nbr = i.toString()
                if (nbr.length % 2 != 0) continue
                if (isInvalidId(nbr)) res.add(i)
            }
        }

        println(res.reduce { acc, nbr -> acc + nbr })
    }

    override fun part2(input: List<String>) {
        val res = mutableListOf<Long>()

        for (range in input.first().split(",")) {
            val (from, to) = range.split("-")
            for (i in from.toLong()..to.toLong()) {
                if (isInvalidIdPart2(i.toString())) res.add(i)
            }
        }

        println(res.reduce { acc, nbr -> acc + nbr })
    }

    private fun isInvalidId(nbr: String): Boolean {
        var left = 0
        var right = nbr.length / 2

        while (left < nbr.length / 2) {
            if (nbr[left] != nbr[right]) return false
            left++
            right++
        }
        return true
    }

    private fun isInvalidIdPart2(nbr: String): Boolean {
        for (i in 1 until nbr.length) {
            if (nbr.length % i == 0) {
                val chunkSize = if (i == 1) 1 else nbr.length / i
                if (nbr.chunked(chunkSize).distinct().size <= 1) return true
            }
        }
        return false
    }

}

fun main() = Day02().run()
