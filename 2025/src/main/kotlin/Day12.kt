import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class Day12 : PuzzleRunner() {
    val executor = Executors.newFixedThreadPool(6)

    data class Coord(val x: Int, val y: Int)

    data class Placement(val x: Int, val y: Int, val shape: List<ByteArray>, val shapeIndex: Int, val score: Int)

    data class CurrentArea(val grid: List<ByteArray>) {
        fun filledHeight(): Int {
            var maxRow = 0
            for (y in grid.indices) {
                val row = grid[y]
                for (x in row.indices) {
                    if (row[x] != 0.toByte()) {
                        if (y + 1 > maxRow) maxRow = y + 1
                    }
                }
            }
            return maxRow
        }


        fun filledWidth(): Int {
            var maxCol = 0
            val cols = grid[0].size
            for (x in 0 until cols) {
                for (y in grid.indices) {
                    if (grid[y][x] != 0.toByte()) {
                        if (x + 1 > maxCol) maxCol = x + 1
                        break
                    }
                }
            }
            return maxCol
        }
    }

    data class Shape(val id: Int, val grid: List<ByteArray>) {

        fun size(): Int {
            return grid.fold(0) { r, item -> r + item.count { it == 1.toByte() } }
        }

    }

    class Region(val width: Int, val height: Int, val shapes: IntArray)

    private fun rotate90(matrix: List<ByteArray>): List<ByteArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        return List(cols) { j ->
            ByteArray(rows) { i ->
                matrix[rows - 1 - i][j]
            }
        }
    }

    fun flipHorizontal(matrix: List<ByteArray>): List<ByteArray> {
        return matrix.map { row ->
            row.reversedArray()
        }
    }

    private fun createVariantsMap(shapes: List<Shape>): Map<Int, List<List<ByteArray>>> {
        return shapes.mapIndexed { index, shape ->
            val transformations = mutableListOf<List<ByteArray>>()
            var current = shape.grid

            // Original + rotations
            repeat(4) {
                transformations.add(current)
                current = rotate90(current)
            }
            // Flip horizontally + rotations
            current = flipHorizontal(shape.grid)
            repeat(4) {
                transformations.add(current)
                current = rotate90(current)
            }
            // Remove duplicates
            val distinctVariants = transformations.distinctBy { t -> t.map { it.toList() } }
            index to distinctVariants
        }.toMap()
    }

    override fun part1(input: List<String>) {
        val shapes = mutableListOf<Shape>()
        val regions = mutableListOf<Region>()
        parseInput(input, shapes, regions)

        val variantsMap: Map<Int, List<List<ByteArray>>> = createVariantsMap(shapes)

        var fits = 0
        for (region in regions) {
            if (doPresentsFit(region, shapes, variantsMap)) {
                fits++
            }
        }
        println(fits)
        executor.shutdown()
    }

    // Super slow greedy approach
    private fun doPresentsFit(
        region: Region,
        shapes: List<Shape>,
        variantsMap: Map<Int, List<List<ByteArray>>>
    ): Boolean {
        val anchor = selectAnchor(shapes, region)
        val area = createInitialArea(anchor, region)

        while (!region.shapes.all { it == 0 }) {

            val taskList = mutableListOf<Future<Placement>>()

            for (i in region.shapes.indices) {
                val value = region.shapes[i]
                if (value == 0) continue

                val task: Callable<Placement> = Callable { getPlacementForShape(i, area, variantsMap) }
                taskList.add(executor.submit(task))
            }

            val placements = taskList.map { it.get() }
            val bestPlacement = placements.maxBy { it.score }

            if (bestPlacement.score == -1) { // No possible placement found
                return false
            }

            placeShape(area.grid, bestPlacement)
            region.shapes[bestPlacement.shapeIndex]--
        }

        return true
    }

    private fun getPlacementForShape(
        shapeIndex: Int,
        area: CurrentArea,
        variantsMap: Map<Int, List<List<ByteArray>>>
    ): Placement {
        var bestPlacement = Placement(-1, -1, listOf(), shapeIndex, -1)

        for (variant in variantsMap.getValue(shapeIndex)) {
            val placement = calculateBestScoreForShapeVariant(area, variant, shapeIndex)
            if (placement != null) {
                if (placement.score > bestPlacement.score) {
                    bestPlacement = placement
                }
            }
        }
        return bestPlacement
    }

    private fun calculateBestScoreForShapeVariant(
        currentArea: CurrentArea,
        shapeVariant: List<ByteArray>,
        shapeIndex: Int
    ): Placement? {
        val grid = currentArea.grid

        var maxScore = -1
        var bestX = -1
        var bestY = -1

        // Sliding 3x3 window
        for (y in 0..currentArea.filledHeight()) {
            for (x in 0..currentArea.filledWidth()) {

                val score = tryPlaceShape(grid, shapeVariant, x, y)
                if (score > maxScore) {
                    maxScore = score
                    bestX = x
                    bestY = y
                }
            }
        }

        if (maxScore >= 0) {
            return Placement(bestX, bestY, shapeVariant, shapeIndex, maxScore)
        }
        return null
    }

    private fun selectAnchor(shapes: List<Shape>, region: Region): Shape {
        for (i in region.shapes.indices) {
            val v = region.shapes[i]
            if (v != 0) {
                region.shapes[i] = v - 1
                return shapes[i]
            }
        }
        error("error")
    }

    private fun createInitialArea(anchor: Shape, region: Region): CurrentArea {
        val anchorHeight = anchor.grid.size
        val anchorWidth = anchor.grid[0].size

        val area = MutableList(region.height) {
            ByteArray(region.width) { 0 }
        }

        for (y in 0 until anchorHeight) {
            for (x in 0 until anchorWidth) {
                area[y][x] = anchor.grid[y][x]
            }
        }

        return CurrentArea(area)
    }

    fun tryPlaceShape(grid: List<ByteArray>, shape: List<ByteArray>, x: Int, y: Int): Int {
        val connected = mutableSetOf<Coord>()

        for (dy in 0..2) {
            val rowY = y + dy
            if (rowY >= grid.size) return -1 // Out of area

            val row = grid[rowY]

            for (dx in 0..2) {
                val colX = x + dx
                if (colX >= row.size) return -1 // Out of area

                if (shape[dy][dx] != 0.toByte() && row[colX] != 0.toByte()) {
                    return -1 // Collision
                }

                val neighbors = listOf(
                    y - 1 to x,
                    y + 1 to x,
                    y to x - 1,
                    y to x + 1
                )

                if (shape[dy][dx] != 0.toByte()) {
                    for ((ny, nx) in neighbors) {
                        if (ny <= 0 || nx <= 0 || ny >= grid.size || nx >= row.size) continue
                        if (grid[ny][nx] == 1.toByte()) {
                            connected.add(Coord(nx, ny))
                        }
                    }
                }
            }
        }

        return connected.size
    }

    fun placeShape(grid: List<ByteArray>, bestPlacement: Placement) {
        for (dy in 0..2) {
            for (dx in 0..2) {
                val v = bestPlacement.shape[dy][dx]
                if (v != 0.toByte()) {
                    grid[bestPlacement.y + dy][bestPlacement.x + dx] = v
                }
            }
        }
    }

    fun get(grid: List<ByteArray>, r: Int, c: Int): Byte {
        return if (r in 0 until grid.size && c in 0 until grid[0].size)
            grid[r][c]
        else
            0 // Outside bounds
    }


    private fun parseInput(
        input: List<String>,
        shapes: MutableList<Shape>,
        regions: MutableList<Region>
    ) {
        var i = 0
        while (i < input.size) {
            if (input[i].isEmpty()) {
                i++
                continue
            }

            if (input[i][0].isDigit() && input[i][1] == ':') {
                val lines = input.subList(i + 1, i + 4)
                val bytes: List<ByteArray> = lines.map { row ->
                    row.map { if (it == '#') 1.toByte() else 0.toByte() }.toByteArray()
                }
                shapes.add(Shape(input[i][0].digitToInt(), bytes))
                i += 4
                continue
            } else {
                val sections = input[i].split(":")
                val (width, height) = sections[0].trim().split("x").map { it.toInt() }
                val shapes = sections[1].trim().split(" ").map { it.toInt() }.toIntArray()
                regions.add(Region(width, height, shapes))
            }
            i++
        }
    }

}


fun main() = Day12().run()
