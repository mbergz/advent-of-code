class Day02 : PuzzleRunner() {
    override fun part1(input: List<String>) {
        val res = mutableListOf<Long>()

        for (range in input.first().split(",")) {
            val (from, to) = range.split("-")
            for (i in from.toLong()..to.toLong()) {
                val nbr = i.toString()
                if (nbr.length % 2 != 0) continue
                if (isInvalidId(nbr)) res.add(nbr.toLong())
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
}

fun main() = Day02().run()
