import io.ksmt.KContext
import io.ksmt.expr.KExpr
import io.ksmt.expr.KInt32NumExpr
import io.ksmt.solver.KSolverStatus
import io.ksmt.solver.z3.KZ3Solver
import io.ksmt.sort.KIntSort
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

class Day10 : PuzzleRunner() {

    data class Button(val positions: List<Int>)

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
            queue.indices.forEach { _ ->
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
        for (il in btn.positions) {
            chars[il] = if (chars[il] == '#') '.' else '#'
        }
        return String(chars)
    }

    /**
     * Couldn't solve this with brute-force or memoization.
     * Looked up reddit, this is an Integer Linear Programming (ILP) problem
     * 
     * Credit to AI and https://www.reddit.com/r/adventofcode/comments/1pity70/2025_day_10_solutions/
     */
    override fun part2(input: List<String>) {
        var res = 0
        for (line in input) {
            val split = line.split("{")
            val buttons = split[0].split("]")[1].substringBefore("{").trim()
                .split(" ")
                .map { Button(it.drop(1).dropLast(1).split(",").map { nbr -> nbr.toInt() }) }
            val pattern = split[1].dropLast(1).trim().split(",").map { it.toInt() }.toIntArray()

            val maxClicks = pattern.max()

            val ctx = KContext()
            with(ctx) {
                val solver = KZ3Solver(ctx)


                // Build the linear algebra matrix equation Ax=b
                // If we take first row on the example input we have
                //                            [ x1 ]
                //      [  0 0 0 0 1 1  ]     | x2 |        [ 3 ]
                // A =  |  0 1 0 0 0 1  | x = | x3 |    b = | 5 |
                //      |  0 0 1 1 1 0  |     | x4 |        | 4 |
                //      [  1 1 0 1 0 0  ]     | x5 |        [ 7 ]
                //                            [ x6 ]
                //
                // Each column in A represent a button, (3) =  [ 0 0 0 1 ]T


                // variables x0,x1,x2..x(n-1)
                val x = Array(buttons.size) { i ->
                    mkConst("x$i", intSort)
                }

                pattern.forEachIndexed { patternIndex, pattern ->
                    // This will build the x values in the row that have a 1, for example x2+x4
                    var rowSum: KExpr<KIntSort> = 0.expr

                    // Take row value from each button (which is col)
                    buttons.forEachIndexed { btnIndex, btn ->
                        if (btn.positions.contains(patternIndex)) {
                            rowSum += x[btnIndex]
                        }
                    }

                    // Build up one row in matrix on format Ax=b, where b is goal pattern nbr
                    solver.assert(rowSum eq pattern.expr)
                }

                // Set lower and higher bounds
                x.forEach {
                    solver.assert(it ge 0.expr)
                    solver.assert(it le maxClicks.expr)
                }

                var minimalSum: Int = Integer.MAX_VALUE

                while (true) {
                    val result = solver.check(timeout = 10.seconds)
                    if (KSolverStatus.UNSAT == result) break

                    val model = solver.model()
                    val value = x.map { xi -> (model.eval(xi) as KInt32NumExpr).value }.toTypedArray().sum()
                    minimalSum = min(minimalSum, value)

                    // Constrain solver: next solution must have strictly smaller sum
                    val sumExpr = x.fold(0.expr as KExpr<KIntSort>) { acc, xi -> acc + xi }
                    solver.assert(sumExpr lt value.expr)
                }

                res += minimalSum
            }
        }
        println(res)
    }


}


fun main() = Day10().run()
