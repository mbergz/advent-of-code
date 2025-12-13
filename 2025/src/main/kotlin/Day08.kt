import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class Day08 : PuzzleRunner() {

    data class JunctionBoxDistance(val p: Coord, val q: Coord, val d: Double) : Comparable<JunctionBoxDistance> {
        override fun compareTo(other: JunctionBoxDistance): Int {
            return this.d.compareTo(other.d)
        }
    }

    data class Coord(val x: Int, val y: Int, val z: Int)


    override fun part1(input: List<String>) {
        val minHeap: PriorityQueue<JunctionBoxDistance> = parseByEuclideanDistance(input)

        val circuits = mutableListOf<MutableSet<Coord>>()

        repeat(1000) {
            val md = minHeap.poll()

            val found = circuits.filter { it.contains(md.p) || it.contains(md.q) }.toList()
            if (found.size == 1) {
                found.first().add(md.p)
                found.first().add(md.q)
            } else if (found.size > 1) {
                val merged = found.flatten().toMutableSet()
                found.forEach { circuits.remove(it) }
                circuits.add(merged)
            } else {
                circuits.add(mutableSetOf(md.p, md.q))
            }
        }

        val sorted = circuits.sortedByDescending { it.size }
        println(sorted[0].size * sorted[1].size * sorted[2].size)
    }

    override fun part2(input: List<String>) {
        val minHeap: PriorityQueue<JunctionBoxDistance> = parseByEuclideanDistance(input)

        val circuits = mutableListOf<MutableSet<Coord>>()

        input.forEach { circuits.add(mutableSetOf(it.toCoord())) }

        while (true) {
            val md = minHeap.poll()

            val found = circuits.filter { it.contains(md.p) || it.contains(md.q) }.toList()
            if (found.size == 1) {
                found.first().add(md.p)
                found.first().add(md.q)
            } else if (found.size > 1) {
                val merged = found.flatten().toMutableSet()
                found.forEach { circuits.remove(it) }
                circuits.add(merged)
            } else {
                circuits.add(mutableSetOf(md.p, md.q))
            }

            if (circuits.size == 1) {
                println(md.p.x.toLong() * md.q.x)
                break
            }
        }
    }


    private fun parseByEuclideanDistance(input: List<String>): PriorityQueue<JunctionBoxDistance> {
        val minHeap = PriorityQueue<JunctionBoxDistance>()

        for (i in input.indices) {
            val p = input[i].toCoord()
            for (j in i + 1 until input.size) {
                val q = input[j].toCoord()

                val distance = sqrt(
                    (p.x - q.x).toDouble().pow(2) +
                            (p.y - q.y).toDouble().pow(2) +
                            (p.z - q.z).toDouble().pow(2)
                )
                minHeap.add(JunctionBoxDistance(p, q, distance))
            }
        }
        return minHeap
    }

    private fun String.toCoord(): Coord {
        val (x, y, z) = this.split(",").map { it.toInt() }
        return Coord(x, y, z)
    }
}


fun main() = Day08().run()
