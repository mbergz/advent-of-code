package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strings"
)

func main() {
	input, err := os.ReadFile("./day24.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

var directions = []struct{ dx, dy int }{
	{0, 1},  // down
	{1, 0},  // right
	{-1, 0}, // left
	{0, -1}, // up
}

func part1(input string) {
	defer util.Timer()()

	visited := make(map[uint32]bool)

	first := createGrid(input)

	visited[convertMatrixToNbr(first)] = true

	current := first
	for {
		newGrid := iteration(current)

		nbrRepr := convertMatrixToNbr(newGrid)
		if _, ok := visited[nbrRepr]; ok {
			fmt.Println(nbrRepr)
			return
		}
		visited[nbrRepr] = true

		current = newGrid
	}
}

func part2(input string) {
	defer util.Timer()()

	firstGrid := createGrid(input)

	currentGrids := make(map[int][][]int)
	currentGrids[0] = firstGrid
	currentGrids[1] = emptyGrid()
	currentGrids[-1] = emptyGrid()

	for i := 0; i < 200; i++ {
		nextGrids := make(map[int][][]int) // Collect all new grids on all levels to use for next iteration

		level := 0
		nextLevelGrid := iterateCurrentLevelGrid(currentGrids[level], currentGrids[level+1], currentGrids[level-1])
		nextGrids[level] = nextLevelGrid

		iterateForwardLevels(level+1, currentGrids, nextGrids)
		iterateBackwardsLevels(level-1, currentGrids, nextGrids)

		currentGrids = nextGrids
	}

	totalCount := 0
	for _, grid := range currentGrids {
		for _, row := range grid {
			for _, val := range row {
				if val == 1 {
					totalCount++
				}
			}
		}
	}

	fmt.Println(totalCount)
}

func createGrid(input string) [][]int {
	grid := make([][]int, 5)
	for rowI, line := range strings.Split(strings.ReplaceAll(input, "\r\n", "\n"), "\n") {
		row := make([]int, 5)
		for colI, char := range line {
			if rune(char) == '#' {
				row[colI] = 1
			}
		}
		grid[rowI] = row
	}
	return grid
}

func iterateForwardLevels(level int, currentGrids, nextGrids map[int][][]int) {
	nextGrid, exists := currentGrids[level+1]
	if !exists {
		nextGrid = emptyGrid()
		nextGrids[level+1] = nextGrid // Add new empty  grid for pickup next iteration
	}

	updatedGrid := iterateCurrentLevelGrid(currentGrids[level], nextGrid, currentGrids[level-1])
	nextGrids[level] = updatedGrid

	if exists { // Next level has non-empty grid, continue exploring recursively
		iterateForwardLevels(level+1, currentGrids, nextGrids)
	}
}

func iterateBackwardsLevels(level int, currentGrids, nextGrids map[int][][]int) {
	nextGrid, exists := currentGrids[level-1]
	if !exists {
		nextGrid = emptyGrid()
		nextGrids[level-1] = nextGrid // Add new empty  grid for pickup next iteration
	}

	updatedGrid := iterateCurrentLevelGrid(currentGrids[level], currentGrids[level+1], nextGrid)
	nextGrids[level] = updatedGrid

	if exists { // Next level has non-empty grid, continue exploring recursively
		iterateBackwardsLevels(level-1, currentGrids, nextGrids)
	}
}

func countBugsRow(row []int) int {
	count := 0
	for _, v := range row {
		if v == 1 {
			count++
		}
	}
	return count
}

func countBugsCol(grid [][]int, colIndex int) int {
	count := 0
	for i := 0; i < 5; i++ {
		if grid[i][colIndex] == 1 {
			count++
		}
	}
	return count
}

func iterateCurrentLevelGrid(grid, innerGrid, surroundingGrid [][]int) [][]int {
	res := make([][]int, 5)
	for rowI := 0; rowI < 5; rowI++ {
		row := make([]int, 5)
		for colI := 0; colI < 5; colI++ {
			if colI == 2 && rowI == 2 {
				continue // Skip checking adj for middle one
			}
			adjBugs := 0
			adjBugs += getAdjacentAbove(colI, rowI, grid, innerGrid, surroundingGrid)
			adjBugs += getAdjacentRight(colI, rowI, grid, innerGrid, surroundingGrid)
			adjBugs += getAdjacentBelow(colI, rowI, grid, innerGrid, surroundingGrid)
			adjBugs += getAdjacentLeft(colI, rowI, grid, innerGrid, surroundingGrid)

			if grid[rowI][colI] == 1 { // #
				if adjBugs == 1 {
					row[colI] = 1
				}
			} else { // .
				if adjBugs == 1 || adjBugs == 2 {
					row[colI] = 1
				}
			}
		}
		res[rowI] = row
	}

	return res
}

func getAdjacentAbove(colI, rowI int, grid, innerGrid, surroundingGrid [][]int) int {
	adjBugs := 0
	newX, newY := colI, rowI-1
	if newY == -1 {
		if surroundingGrid[1][2] == 1 {
			adjBugs++
		}
	} else if newX == 2 && newY == 2 {
		adjBugs += countBugsRow(innerGrid[4])
	} else {
		if grid[newY][newX] == 1 {
			adjBugs++
		}
	}
	return adjBugs
}

func getAdjacentRight(colI, rowI int, grid, innerGrid, surroundingGrid [][]int) int {
	adjBugs := 0
	newX, newY := colI+1, rowI
	if newX == 5 {
		if surroundingGrid[2][3] == 1 {
			adjBugs++
		}
	} else if newX == 2 && newY == 2 {
		adjBugs += countBugsCol(innerGrid, 0)
	} else {
		if grid[newY][newX] == 1 {
			adjBugs++
		}
	}
	return adjBugs
}

func getAdjacentBelow(colI, rowI int, grid, innerGrid, surroundingGrid [][]int) int {
	adjBugs := 0
	newX, newY := colI, rowI+1
	if newY == 5 {
		if surroundingGrid[3][2] == 1 {
			adjBugs++
		}
	} else if newX == 2 && newY == 2 {
		adjBugs += countBugsRow(innerGrid[0])
	} else {
		if grid[newY][newX] == 1 {
			adjBugs++
		}
	}
	return adjBugs
}

func getAdjacentLeft(colI, rowI int, grid, innerGrid, surroundingGrid [][]int) int {
	adjBugs := 0
	newX, newY := colI-1, rowI
	if newX == -1 {
		if surroundingGrid[2][1] == 1 {
			adjBugs++
		}
	} else if newX == 2 && newY == 2 {
		adjBugs += countBugsCol(innerGrid, 4)
	} else {
		if grid[newY][newX] == 1 {
			adjBugs++
		}
	}
	return adjBugs
}

func emptyGrid() [][]int {
	empty := make([][]int, 5)
	for i := range empty {
		empty[i] = make([]int, 5)
	}
	return empty
}

// Represent the 5x5 grid to binary then decimal number
func convertMatrixToNbr(matrix [][]int) uint32 {
	var res uint32 = 0

	index := 0
	for _, row := range matrix {
		for _, val := range row {
			if val == 1 {
				// Shift binary 1   (000...00001) by index bits
				// Index 4 would be (000...10000)
				// Then take current value of res and perform bitwise OR with this value
				res |= 1 << index
			}
			index++
		}
	}
	return res
}

func iteration(grid [][]int) [][]int {
	res := make([][]int, 5)
	for rowI := 0; rowI < 5; rowI++ {
		row := make([]int, 5)
		for colI := 0; colI < 5; colI++ {
			adjBugs := 0

			for _, d := range directions {
				newX := colI + d.dx
				newY := rowI + d.dy
				if newX >= 0 && newX < 5 && newY >= 0 && newY < 5 {
					if grid[newY][newX] == 1 {
						adjBugs++
					}
				}
			}

			if grid[rowI][colI] == 1 { // #
				if adjBugs == 1 {
					row[colI] = 1
				}
			} else { // .
				if adjBugs == 1 || adjBugs == 2 {
					row[colI] = 1
				}
			}
		}
		res[rowI] = row
	}

	return res
}
