import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.time.measureTime

abstract class PuzzleRunner {
    abstract fun part1(input: String)
    open fun part2(input: String): Unit = Unit

    fun run() {
        val className = this::class.simpleName ?: error("No class name")
        val input = readInput(className)

        println("----- $className -----")
        println("--- part1 ---")
        val timeTakenPart1 = measureTime { part1(input) }
        println("-- $timeTakenPart1 --")
        println("--- part2 ---")
        val timeTakenPart2 = measureTime { part2(input) }
        println("-- $timeTakenPart2 --")
    }

    private fun readInput(name: String) = Path("src/main/resources/$name.txt").readText().trim()
}