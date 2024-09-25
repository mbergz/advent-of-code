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

var directions = map[byte]Coord{
	'R': {1, 0},
	'D': {0, 1},
	'L': {-1, 0},
	'U': {0, -1},
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

	var lowestManhattan float64 = math.MaxFloat64

	for key := range visitedFirstWire {
		_, ok := visitedSecondWire[key]
		if ok {
			lowestManhattan = math.Min(float64(lowestManhattan), manhattanDist(key))

		}
	}

	fmt.Println(lowestManhattan)
}

func part2(input string) {
	visitedFirstWire := createVisitedFromWire(strings.Split(input, "\n")[0])
	visitedSecondWire := createVisitedFromWire(strings.Split(input, "\n")[1])

	var lowestCombinedDist float64 = math.MaxFloat64

	for key, valueFirst := range visitedFirstWire {
		valueSecond, ok := visitedSecondWire[key]
		if ok {
			lowestCombinedDist = math.Min(lowestCombinedDist, float64(valueFirst+valueSecond))
		}
	}

	fmt.Println(lowestCombinedDist)
}

func createVisitedFromWire(wire string) map[Coord]int {
	visited := make(map[Coord]int)

	current := Coord{0, 0}
	currentDist := 0
	for _, cmd := range strings.Split(strings.TrimSpace(wire), ",") {
		dist := cmd[1:]
		delta := directions[cmd[0]]

		for i := 1; i <= util.ToInt(dist); i++ {
			current.x = current.x + delta.x
			current.y = current.y + delta.y
			visited[current] = currentDist + i
		}

		currentDist += int(math.Abs(float64(util.ToInt(dist))))
	}
	return visited
}

func manhattanDist(coord Coord) float64 {
	return math.Abs(float64(0-coord.x)) + math.Abs(float64(0-coord.y))
}
