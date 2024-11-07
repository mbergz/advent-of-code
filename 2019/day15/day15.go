package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
)

var directions = map[int]Coord{
	4: {1, 0},  // east
	3: {-1, 0}, // west
	2: {0, 1},  // south
	1: {0, -1}, // north
}

var fromDirection = map[int]int{
	4: 3,
	3: 4,
	2: 1,
	1: 2,
}

type Coord struct {
	x int
	y int
}

type CoordLevel struct {
	coord Coord
	level int
}

type Path struct {
	coord         Coord
	comp          *intcodecomputer.IntcodeComp
	fromDirection int
	length        int
}

func main() {
	input, err := os.ReadFile("./day15.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	pathsToOxygenSystem := make([]int, 0)

	// BFS
	queue := make([]Path, 0)
	queue = append(queue, Path{Coord{0, 0}, intcodecomputer.InitilizeComputer(input), 0, 0})
	visited := make(map[Coord]bool)

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		for nextDir := 1; nextDir <= 4; nextDir++ {
			if nextDir == curr.fromDirection {
				continue
			}
			computer := curr.comp.Copy()
			output := computer.StepOne(nextDir)

			if output == 1 {
				newCoord := getNewCoord(curr.coord, nextDir)
				fromDir := fromDirection[nextDir]

				if _, ok := visited[newCoord]; !ok {
					visited[newCoord] = true
					queue = append(queue, Path{newCoord, computer, fromDir, curr.length + 1})
				}
			} else if output == 2 {
				pathsToOxygenSystem = append(pathsToOxygenSystem, curr.length+1)
			}
		}
	}

	fmt.Println(pathsToOxygenSystem)
}

func getNewCoord(curr Coord, newDirection int) Coord {
	new := directions[newDirection]
	return Coord{curr.x + new.x, curr.y + new.y}
}

func part2(input string) {
	defer util.Timer()()

	// BFS to find coord for oxygensystem and build up visited map
	queue := make([]Path, 0)
	queue = append(queue, Path{Coord{0, 0}, intcodecomputer.InitilizeComputer(input), 0, 0})
	visited := make(map[Coord]bool)
	var foundOxyGenSystem Coord

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		for nextDir := 1; nextDir <= 4; nextDir++ {
			if nextDir == curr.fromDirection {
				continue
			}
			computer := curr.comp.Copy()
			output := computer.StepOne(nextDir)

			if output == 1 {
				newCoord := getNewCoord(curr.coord, nextDir)
				fromDir := fromDirection[nextDir]

				if _, ok := visited[newCoord]; !ok {
					visited[newCoord] = true
					queue = append(queue, Path{newCoord, computer, fromDir, curr.length + 1})
				}
			} else if output == 2 {
				foundOxyGenSystem = getNewCoord(curr.coord, nextDir)
			}
		}
	}

	// BFS from oxygensystem outwards, use visited map to traverse and count levels
	fmt.Println(countLevelsBfs(foundOxyGenSystem, visited))
}

func countLevelsBfs(start Coord, visitedMap map[Coord]bool) int {
	queue := make([]CoordLevel, 0)
	queue = append(queue, CoordLevel{start, 0})
	visited := make(map[Coord]bool)

	maxLevel := math.MinInt

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		maxLevel = max(maxLevel, curr.level)

		coordNorth := getNewCoord(curr.coord, 1) // north
		coordSouth := getNewCoord(curr.coord, 2) // south
		coordWest := getNewCoord(curr.coord, 3)  // west
		coordEast := getNewCoord(curr.coord, 4)  // east

		for key, _ := range visitedMap {
			if key == coordNorth || key == coordSouth || key == coordWest || key == coordEast {
				if _, ok := visited[key]; !ok {
					visited[key] = true
					queue = append(queue, CoordLevel{key, curr.level + 1})
				}
			}
		}
	}
	return maxLevel
}
