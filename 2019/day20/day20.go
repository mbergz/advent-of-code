package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
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
	name  string
	outer Coord
	inner Coord
}

type Node struct {
	level    int
	coord    Coord
	distance int
}

type PortalConnection struct {
	name    string
	coord   Coord
	length  int
	isInner bool // Inner portal or outer portal
}

func main() {
	input, err := os.ReadFile("./day20.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
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
				if nextCoord == portal.outer {
					nextCoord = portal.inner
				} else {
					nextCoord = portal.outer
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

func part2(input string) {
	defer util.Timer()()

	grid := parseGrid(input)

	portalMap := makePortalMap(grid)
	var startCoord Coord

	connectedPortals := buildConnectedMap(portalMap, grid)

	for k, v := range portalMap {
		if v.name == "AA" {
			startCoord = k
		}
	}

	// BFS on connected portals as neighbours instead of regular large grid.
	// Use Node to keep track of current level and current distance
	queue := make([]Node, 0)
	queue = append(queue, Node{0, startCoord, 0})

	shortest := math.MaxInt

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		// Stop exploring after a certain depth
		if curr.level > 25 {
			continue
		}

		for _, cp := range connectedPortals[curr.coord] {

			var newCoord Coord

			newLevel := curr.level
			if cp.isInner {
				newLevel++
				newCoord = portalMap[cp.coord].outer
			} else {
				if curr.level == 0 {
					if cp.name == "ZZ" {
						if curr.distance-1+cp.length < shortest {
							shortest = curr.distance - 1 + cp.length
							fmt.Println(shortest)
							return
						}
					}
					// Skip because impossible to go outside level 0
					continue
				}

				newLevel--
				newCoord = portalMap[cp.coord].inner
			}

			newDistance := curr.distance + cp.length
			queue = append(queue, Node{newLevel, newCoord, newDistance})

		}
	}
}

func buildConnectedMap(portalMap map[Coord]Portal, grid [][]rune) map[Coord][]PortalConnection {
	result := make(map[Coord][]PortalConnection)
	for key, _ := range portalMap {

		connected := make([]PortalConnection, 0)
		// BFS level order traversal
		queue := make([]Coord, 0)
		queue = append(queue, key)
		visited := make(map[Coord]bool)
		depth := 0

		for len(queue) > 0 {

			levelSize := len(queue)
			for i := 0; i < levelSize; i++ {
				curr := queue[0]
				queue = queue[1:]

				if found, ok := portalMap[curr]; ok && found.inner != key && found.outer != key {
					isInner := true
					if found.outer == curr {
						isInner = false
					}
					pC := PortalConnection{found.name, curr, depth + 1, isInner}
					connected = append(connected, pC)
					continue
				}

				for _, d := range directions {
					dy, dx := curr.y+d.y, curr.x+d.x

					if grid[dy][dx] != '.' {
						continue
					}

					nextCoord := Coord{dy, dx}
					if _, ok := visited[nextCoord]; !ok {
						visited[nextCoord] = true
						queue = append(queue, nextCoord)
					}

				}

			}
			depth++
		}

		result[key] = connected

	}

	return result
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

						existing, ok := tempMap[portalName]
						if !ok {
							tempMap[portalName] = Portal{name: portalName}
							existing = tempMap[portalName]
						}

						if coord.x <= 2 || coord.x >= len(line)-3 || coord.y <= 2 || coord.y >= len(grid)-3 {
							existing.outer = coord
						} else {
							existing.inner = coord
						}
						tempMap[portalName] = existing
					}
				}
			}
		}
	}

	// Return with coord as key for fast lookups
	portalMap := make(map[Coord]Portal)

	emptyCoord := Coord{0, 0}
	for _, v := range tempMap {
		if v.outer != emptyCoord {
			portalMap[v.outer] = v
		}
		if v.inner != emptyCoord {
			portalMap[v.inner] = v
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
