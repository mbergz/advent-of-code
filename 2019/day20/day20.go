package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strings"
	"unicode"
)

var directions = []Coord{
	{-1, 0}, // up
	{0, 1},  // right
	{1, 0},  // down
	{0, -1}, // left
}

type Coord struct {
	y int
	x int
}

type Portal struct {
	name string
	a    Coord
	b    Coord
}

func main() {
	input, err := os.ReadFile("./day20.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
}

func part1(input string) {
	defer util.Timer()()

	grid := parseGrid(input)

	portalMap := makePortalMap(grid)
	var startCoord, goalCoord Coord

	for k, v := range portalMap {
		if v.name == "AA" {
			startCoord = k
		}
		if v.name == "ZZ" {
			goalCoord = k
		}
	}
	delete(portalMap, startCoord)
	delete(portalMap, goalCoord)

	queue := make([]Coord, 0)
	queue = append(queue, startCoord)

	distance := make(map[Coord]int)
	for y := range len(grid) {
		for x := range len(grid[y]) {
			distance[Coord{y, x}] = -1
		}
	}
	distance[startCoord] = 0

	// Standard BFS with distance matrix
	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		if curr == goalCoord {
			fmt.Println(distance[curr])
			return
		}

		for _, d := range directions {
			dy, dx := curr.y+d.y, curr.x+d.x

			if grid[dy][dx] != '.' {
				continue
			}

			nextCoord := Coord{dy, dx}
			jumpSteps := 1
			if portal, ok := portalMap[nextCoord]; ok {
				if nextCoord == portal.a {
					nextCoord = portal.b
				} else {
					nextCoord = portal.a
				}
				jumpSteps = 2
			}

			if distance[nextCoord] == -1 {
				distance[nextCoord] = distance[curr] + jumpSteps
				queue = append(queue, nextCoord)
			}

		}
	}
}

func parseGrid(input string) [][]rune {
	inputArr := strings.Split(strings.ReplaceAll(input, "\r\n", "\n"), "\n")
	grid := make([][]rune, len(inputArr))

	for y, line := range inputArr {
		grid[y] = []rune(line)
	}
	return grid
}

func makePortalMap(grid [][]rune) map[Coord]Portal {
	// Find all the portals
	tempMap := make(map[string]Portal)

	for y, line := range grid {
		for x, char := range line {
			if unicode.IsLetter(char) {
				neighbourRune, found := findNeighbourRune(grid, y, x)
				if found {
					// if "." next to first char, add to tempMap
					coord, found := findNeighbourChar(grid, y, x, '.')
					if found {
						portalName := string(char) + string(neighbourRune)
						if char > neighbourRune {
							portalName = string(neighbourRune) + string(char)
						}

						if existing, ok := tempMap[portalName]; !ok {
							tempMap[portalName] = Portal{name: portalName, a: coord}
						} else {
							tempMap[portalName] = Portal{existing.name, existing.a, coord}
						}
					}
				}
			}
		}
	}

	// Return with coord as key for fast lookups
	portalMap := make(map[Coord]Portal)

	emptyCoord := Coord{0, 0}
	for _, v := range tempMap {
		portalMap[v.a] = v
		if v.b != emptyCoord {
			portalMap[v.b] = v
		}
	}

	return portalMap
}

func findNeighbourChar(grid [][]rune, y int, x int, target rune) (Coord, bool) {
	for _, d := range directions {
		dy, dx := y+d.y, x+d.x
		if dy >= 0 && dy < len(grid) && dx >= 0 && dx < len(grid[0]) &&
			grid[dy][dx] == target {
			return Coord{dy, dx}, true
		}
	}
	return Coord{}, false
}

func findNeighbourRune(grid [][]rune, y int, x int) (rune, bool) {
	for _, d := range directions {
		dy, dx := y+d.y, x+d.x
		if dy >= 0 && dy < len(grid) && dx >= 0 && dx < len(grid[0]) &&
			unicode.IsLetter(grid[dy][dx]) {
			return grid[dy][dx], true
		}
	}
	return rune(0), false
}
