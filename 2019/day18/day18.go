package main

import (
	"advent-of-code_2019/util"
	"errors"
	"fmt"
	"math"
	"os"
	"sort"
	"strings"
)

type Coord struct {
	x int
	y int
}

type Direction int

const (
	UP Direction = iota
	RIGHT
	DOWN
	LEFT
	NONE
)

var coordDirMap = map[Direction]Coord{
	UP:    {0, -1},
	RIGHT: {1, 0},
	DOWN:  {0, 1},
	LEFT:  {-1, 0},
}

type Node struct {
	coord  Coord
	length int
	keys   string
}

type VisitedNode struct {
	coord Coord
	keys  string
}

var nbrOfKeys int = 0
var shortest int = math.MaxInt

func main() {
	input, err := os.ReadFile("./day18.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	grid := buildGrid(input)

	floodFillBfs(grid)

}

func floodFillBfs(grid [][]rune) {
	start := getStartingNode(grid)

	queue := make([]Node, 0)

	queue = append(queue, Node{start, 0, ""})
	visited := make(map[VisitedNode]int)

	for len(queue) > 0 {
		current := queue[0]
		queue = queue[1:]

		for _, next := range getAllNext(current, grid) {
			var nextChar rune = grid[next.y][next.x]

			if nextChar != '#' {
				newNode := Node{
					next,
					current.length + 1,
					current.keys,
				}

				newVisited := VisitedNode{next, sortKeys(current.keys)}
				if length, ok := visited[newVisited]; ok {
					if length <= current.length+1 {
						continue
					}
				}
				visited[newVisited] = current.length + 1

				if isLowerCase(nextChar) {
					if !strings.ContainsRune(current.keys, nextChar) {
						newNode.keys = current.keys + string(nextChar)
						if len(newNode.keys) == nbrOfKeys {
							shortest = min(shortest, current.length+1)
							continue
						}
						queue = append(queue, newNode)
					} else {
						queue = append(queue, newNode)
					}
				} else if isUpperCase(nextChar) {
					if strings.ContainsRune(strings.ToUpper(current.keys), nextChar) {
						// door is unlocked
						queue = append(queue, newNode)
					}
				} else {
					queue = append(queue, newNode)
				}
			}
		}

	}
	fmt.Println(shortest)
}

func sortKeys(keys string) string {
	runes := []rune(keys)
	sort.Slice(runes, func(i, j int) bool { return runes[i] < runes[j] })
	return string(runes)
}

func getStartingNode(grid [][]rune) Coord {
	var startCoord Coord
	for iRow, row := range grid {
		for iCol, col := range row {
			if string(col) == "@" {
				startCoord = Coord{iCol, iRow}
			}
			if isLowerCase(col) {
				nbrOfKeys++
			}
		}
	}
	return startCoord
}

func getAllNext(current Node, grid [][]rune) []Coord {
	res := make([]Coord, 0)
	for d := UP; d <= LEFT; d++ {
		delta := coordDirMap[d]
		newCoord := Coord{current.coord.x + delta.x, current.coord.y + delta.y}
		_, error := getCharInGrid(grid, newCoord)
		if error == nil {

			res = append(res, newCoord)
		}
	}

	return res
}

func getCharInGrid(grid [][]rune, coord Coord) (rune, error) {
	minX, minY := 0, 0
	maxY := len(grid) - 1
	maxX := len(grid[0]) - 1

	if coord.x >= minX && coord.x <= maxX &&
		coord.y >= minY && coord.y <= maxY {
		return grid[coord.y][coord.x], nil

	}

	return 0, errors.New("out of bounds")
}

func isLowerCase(r rune) bool {
	return r >= 'a' && r <= 'z'
}

func isUpperCase(r rune) bool {
	return r >= 'A' && r <= 'Z'
}

func buildGrid(input string) [][]rune {
	res := make([][]rune, 0)

	for _, line := range strings.Split(input, "\n") {
		row := make([]rune, 0)
		for _, char := range line {
			row = append(row, char)
		}
		res = append(res, row)
	}
	return res
}

func part2(input string) {
	defer util.Timer()()

}
