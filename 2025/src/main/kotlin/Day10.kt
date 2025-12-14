class Day10 : PuzzleRunner() {

    data class Button(val indicatorLights: List<Int>)

    override fun part1(input: List<String>) {
        var res = 0
        for (line in input) {
            val split = line.split("]")
            val pattern = split[0].drop(1)

            val buttons = split[1].substringBefore("{").trim()
                .split(" ")
                .map { Button(it.drop(1).dropLast(1).split(",").map { nbr -> nbr.toInt() }) }
            res += bruteForce(pattern, buttons)
        }
        println(res)
    }

    private fun bruteForce(goalPattern: String, buttons: List<Button>): Int {
        val queue = ArrayDeque<String>()
        queue.add(".".repeat(goalPattern.length))

        var level = 0

        while (queue.isNotEmpty()) {
            level++

            for (i in queue.indices) {
                val curr = queue.removeFirst()

                for (btn in buttons) {
                    val newPattern = pushButton(curr, btn)

                    if (newPattern == goalPattern) return level

                    queue.add(newPattern)
                }
            }

        }
        return 0
    }

    private fun pushButton(pattern: String, btn: Button): String {
        val chars = pattern.toCharArray()
        for (il in btn.indicatorLights) {
            chars[il] = if (chars[il] == '#') '.' else '#'
        }
        return String(chars)
    }

}


fun main() = Day10().run()
