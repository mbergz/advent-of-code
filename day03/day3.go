package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"strings"
)

type Coord struct {
	x int
	y int
}

func main() {
	input, err := os.ReadFile("./day3.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	visitedFirstWire := createVisitedFromWire(strings.Split(input, "\n")[0])
	visitedSecondWire := createVisitedFromWire(strings.Split(input, "\n")[1])

	var intersecting []Coord

	for key := range visitedFirstWire {
		_, ok := visitedSecondWire[key]
		if ok {
			intersecting = append(intersecting, key)
		}
	}

	var lowestManhattan float64 = math.MaxFloat64
	for _, coord := range intersecting {
		lowestManhattan = math.Min(float64(lowestManhattan), manhattanDist(coord))
	}

	fmt.Println(lowestManhattan)
}

func createVisitedFromWire(wire string) map[Coord]bool {
	visited := make(map[Coord]bool)

	current := Coord{0, 0}
	for _, cmd := range strings.Split(strings.TrimSpace(wire), ",") {
		dist := cmd[1:]
		switch cmd[0] {
		case 'R':
			for i := 1; i <= util.ToInt(dist); i++ {
				current.x = current.x + 1
				visited[current] = true
			}
		case 'D':
			for i := 1; i <= util.ToInt(dist); i++ {
				current.y = current.y + 1
				visited[current] = true
			}
		case 'L':
			for i := 1; i <= util.ToInt(dist); i++ {
				current.x = current.x - 1
				visited[current] = true
			}
		case 'U':
			for i := 1; i <= util.ToInt(dist); i++ {
				current.y = current.y - 1
				visited[current] = true
			}
		}
	}
	return visited
}

func manhattanDist(coord Coord) float64 {
	return math.Abs(float64(0-coord.x)) + math.Abs(float64(0-coord.y))
}

func part2(input string) {

}
