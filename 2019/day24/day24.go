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

	first := make([][]int, 5)
	for rowI, line := range strings.Split(strings.ReplaceAll(input, "\r\n", "\n"), "\n") {
		row := make([]int, 5)
		for colI, char := range line {
			if rune(char) == '#' {
				row[colI] = 1
			}
		}
		first[rowI] = row
	}

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
