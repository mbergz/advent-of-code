import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day09 : PuzzleRunner() {

    data class Coord(val x: Int, val y: Int)
    data class CoordRange(val from: Coord, val to: Coord) {
        companion object {
            fun of(a: Coord, b: Coord): CoordRange {
                require(a.x == b.x || a.y == b.y)

                return if (a.x == b.x) { // Same row
                    if (a.y > b.y) {
                        CoordRange(b, a)
                    } else {
                        CoordRange(a, b)
                    }
                } else { // Same col
                    if (a.x > b.x) {
                        CoordRange(b, a)
                    } else {
                        CoordRange(a, b)
                    }
                }
            }
        }

        fun isInside(coord: Coord): Boolean {
            if (from.x == coord.x && to.x == coord.x) {
                return from.y <= coord.y && coord.y <= to.y
            }
            if (from.y == coord.y && to.y == coord.y) {
                return from.x <= coord.x && coord.x <= to.x
            }
            return false
        }
    }

    override fun part1(input: List<String>) {
        var largest = 0L
        for (i in input.indices) {
            val (x1, y1) = input[i].split(",").map { it.toLong() }
            for (j in i + 1 until input.size) {
                val (x2, y2) = input[j].split(",").map { it.toLong() }
                largest = max(largest, (abs(x2 - x1) + 1) * (abs(y2 - y1) + 1))
            }
        }
        println(largest)
    }

    override fun part2(input: List<String>) {
        val reds = mutableListOf<Coord>()
        val ranges = mutableSetOf<CoordRange>()

        for (i in input.indices) {
            val prev = if (i - 1 < 0) input.last() else input[i - 1]
            val curr = input[i]
            val next = if (i + 1 > input.size - 1) input.first() else input[i + 1]

            reds.add(curr.toCoord())

            ranges.add(CoordRange.of(prev.toCoord(), curr.toCoord()))
            ranges.add(CoordRange.of(next.toCoord(), curr.toCoord()))
        }

        var maxArea = 0L
        for (i in reds.indices) {
            for (j in i + 1 until reds.size) {
                val first = reds[i]
                val second = reds[j]

                if (first.x == second.x) {
                    maxArea = max(maxArea, abs(first.y - second.y).toLong())
                } else if (first.y == second.y) {
                    maxArea = max(maxArea, abs(first.x - second.x).toLong())
                } else {
                    var cornerA = Coord(second.x, first.y)
                    var cornerB = Coord(first.x, second.y)
                    if (cornerA.y > cornerB.y) {
                        val tmp = cornerA
                        cornerA = cornerB
                        cornerB = tmp
                    }

                    if (anyRangeInsideArea(first, second, ranges)) continue

                    val aInside = ranges.any { it.isInside(cornerA) }
                    val bInside = ranges.any { it.isInside(cornerB) }

                    if (aInside && bInside) {
                        val newArea = (abs(second.x - first.x) + 1).toLong() * (abs(second.y - first.y) + 1).toLong()
                        maxArea = max(maxArea, newArea)
                        continue
                    }

                    if (!aInside && !isInsideArea(cornerA, ranges) { a, b -> a.y < b.y }) continue
                    if (!bInside && !isInsideArea(cornerB, ranges) { a, b -> a.y > b.y }) continue

                    // Now we have verified all corners
                    val newArea = (abs(second.x - first.x) + 1).toLong() * (abs(second.y - first.y) + 1).toLong()
                    maxArea = max(maxArea, newArea)
                }
            }
        }

        println(maxArea)
    }

    private fun anyRangeInsideArea(
        first: Coord,
        second: Coord,
        ranges: MutableSet<CoordRange>
    ): Boolean {
        val lowXBoundary = min(first.x.toLong(), second.x.toLong())
        val highXBoundary = max(first.x.toLong(), second.x.toLong())

        val lowYBoundary = min(first.y.toLong(), second.y.toLong())
        val highYBoundary = max(first.y.toLong(), second.y.toLong())

        return ranges.any {
            val minX = min(it.from.x, it.to.x)
            val maxX = max(it.from.x, it.to.x)
            val minY = min(it.from.y, it.to.y)
            val maxY = max(it.from.y, it.to.y)

            maxX > lowXBoundary && minX < highXBoundary && maxY > lowYBoundary && minY < highYBoundary
        }
    }

    private fun isInsideArea(
        coord: Coord,
        ranges: MutableSet<CoordRange>,
        cmpPredicate: (a: Coord, b: Coord) -> Boolean
    ): Boolean {
        return ranges.count {
            it.from.y == it.to.y &&
                    it.from.x <= coord.x &&
                    coord.x <= it.to.x &&
                    cmpPredicate(it.from, coord)
        } % 2 != 0
    }

    private fun String.toCoord(): Coord {
        val (x, y) = this.split(",").map { it.toInt() }
        return Coord(x, y)
    }

}


fun main() = Day09().run()
