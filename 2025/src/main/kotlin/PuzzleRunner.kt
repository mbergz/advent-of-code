import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTime

abstract class PuzzleRunner {
    abstract fun part1(input: List<String>)
    open fun part2(input: List<String>): Unit = Unit

    fun run() {
        val className = this::class.simpleName ?: error("No class name")
        val input = readInput(className)

        println("----- $className -----")
        val timeTakenPart1 = measureTime { part1(input) }
        println("--- Part 1: $timeTakenPart1 ---")
        val timeTakenPart2 = measureTime { part2(input) }
        println("--- Part 2: $timeTakenPart2 ---")
    }

    private fun readInput(name: String) = Path("src/main/resources/$name.txt").readLines().map { it.trim() }
}