package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"math/big"
	"os"
	"sort"
	"strings"
)

type Coord struct {
	x int
	y int
}

type CoordMeta struct {
	coord    Coord
	slope    *big.Rat
	distance float64
}

func main() {
	input, err := os.ReadFile("./day10.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func getAllAbove(asteroids []Coord, ref Coord) int {
	if ref.y == 0 {
		return 0
	}
	res := make([]Coord, 0)
	for _, ast := range asteroids {
		if ast.y < ref.y {
			res = append(res, ast)
		}
	}
	return countVisible(res, ref)
}
func getAllToRight(asteroids []Coord, ref Coord) int {
	res := make([]Coord, 0)
	for _, ast := range asteroids {
		if ast.y == ref.y && ast.x > ref.x {
			res = append(res, ast)
		}
	}
	return countVisible(res, ref)
}

func getAllToLeft(asteroids []Coord, ref Coord) int {
	if ref.x == 0 {
		return 0
	}
	res := make([]Coord, 0)
	for _, ast := range asteroids {
		if ast.y == ref.y && ast.x < ref.x {
			res = append(res, ast)
		}
	}
	return countVisible(res, ref)
}

func getAllBelow(asteroids []Coord, ref Coord) int {
	res := make([]Coord, 0)
	for _, ast := range asteroids {
		if ast.y > ref.y {
			res = append(res, ast)
		}
	}
	return countVisible(res, ref)
}

func countVisible(asteroids []Coord, current Coord) int {
	slopes := make([]big.Rat, 0)
	foundVertical := false
	for _, ast := range asteroids {
		y2 := ast.y
		y1 := current.y
		x2 := ast.x
		x1 := current.x

		if x2-x1 == 0 { // vertical line
			foundVertical = true
			continue
		}
		val := big.NewRat(int64(y2-y1), int64(x2-x1))

		if !contains(slopes, *val) {
			slopes = append(slopes, *val)
		}
	}
	length := len(slopes)
	if foundVertical {
		length++
	}
	return length
}

func part1(input string) {
	defer util.Timer()()

	asteroids := make([]Coord, 0)
	for row, line := range strings.Split(input, "\n") {
		for column := 0; column < len(line); column++ {
			if string(line[column]) == "#" {
				asteroids = append(asteroids, Coord{column, row})
			}
		}
	}

	maxCount := 0
	var maxCoord Coord

	for _, current := range asteroids {
		aboveCount := getAllAbove(asteroids, current)
		rightCount := getAllToRight(asteroids, current)
		leftCount := getAllToLeft(asteroids, current)
		belowCount := getAllBelow(asteroids, current)

		total := aboveCount + rightCount + leftCount + belowCount
		if total > maxCount {
			maxCoord = current
			maxCount = total
		}
	}
	fmt.Println(maxCount)
	fmt.Println(maxCoord)
}

func part2(input string) {
	defer util.Timer()()

	// Station located at 28,29 from part1
	asteroids := make([]Coord, 0)
	for row, line := range strings.Split(input, "\n") {
		for column := 0; column < len(line); column++ {
			if string(line[column]) == "#" {
				asteroids = append(asteroids, Coord{column, row})
			}
		}
	}

	var station Coord = Coord{28, 29}
	count := 1
	for {
		countFirstQuadrant(asteroids, station, &count)
		countSecondQuadrant(asteroids, station, &count)
		countThirdQuadrant(asteroids, station, &count)
		countForthQuadrant(asteroids, station, &count)
	}
}

func getSlope(from Coord, to Coord) *big.Rat {
	y2 := to.y
	y1 := from.y
	x2 := to.x
	x1 := from.x

	if x2-x1 == 0 {
		return nil
	}

	return big.NewRat(int64(y2-y1), int64(x2-x1))
}

func getDistance(from Coord, to Coord) float64 {
	var a float64 = math.Abs(float64(to.x) - float64(from.x))
	var b float64 = math.Abs(float64(to.y) - float64(from.y))
	return math.Sqrt(math.Pow(a, 2) + math.Pow(b, 2))
}

func countFirstQuadrant(asteroids []Coord, station Coord, count *int) {
	res := buildCoordMeta(asteroids, station, func(station, ast Coord) bool {
		return ast.x > station.x && ast.y <= station.y
	})

	sortAcendingOrder(res)
	filtered := createFiltered(res)
	runLaser(filtered, asteroids, count)
}

func countSecondQuadrant(asteroids []Coord, station Coord, count *int) {
	res := buildCoordMeta(asteroids, station, func(station, ast Coord) bool {
		return ast.x >= station.x && ast.y > station.y
	})

	sortDescendingOrder(res)
	filtered := createFiltered(res)
	runLaser(filtered, asteroids, count)
}

func countThirdQuadrant(asteroids []Coord, station Coord, count *int) {
	res := buildCoordMeta(asteroids, station, func(station, ast Coord) bool {
		return ast.x < station.x && ast.y >= station.y
	})

	sortAcendingOrder(res)
	filtered := createFiltered(res)
	runLaser(filtered, asteroids, count)
}

func countForthQuadrant(asteroids []Coord, station Coord, count *int) {
	res := buildCoordMeta(asteroids, station, func(station, ast Coord) bool {
		return ast.x <= station.x && ast.y < station.y
	})

	sortDescendingOrder(res)
	filtered := createFiltered(res)
	runLaser(filtered, asteroids, count)
}

func sortAcendingOrder(coords []CoordMeta) {
	sort.Slice(coords, func(i, j int) bool {
		slopeCompare := coords[i].slope.Cmp(coords[j].slope)
		if slopeCompare == 0 { // same slope
			// Compare by distance
			return coords[i].distance < coords[j].distance
		}
		return coords[i].slope.Cmp(coords[j].slope) > 0
	})
}

func sortDescendingOrder(coords []CoordMeta) {
	sort.Slice(coords, func(i, j int) bool {
		if coords[i].slope == nil {
			return false
		}
		if coords[j].slope == nil {
			return true
		}
		slopeCompare := coords[i].slope.Cmp(coords[j].slope)
		if slopeCompare == 0 { // same slope
			// Compare by distance
			return coords[i].distance < coords[j].distance
		}
		return coords[i].slope.Cmp(coords[j].slope) < 0
	})
}

func runLaser(filtered []CoordMeta, asteroids []Coord, count *int) {
	for _, ast := range filtered {
		asteroids = removeElem(asteroids, ast.coord)
		*count++
		if *count == 200 {
			fmt.Println(ast.coord)
			os.Exit(0)
		}
	}
}

func removeElem(asteroids []Coord, elemToRemove Coord) []Coord {
	res := make([]Coord, 0)

	for _, ast := range asteroids {
		if elemToRemove != ast {
			res = append(res, ast)
		}
	}
	return res
}

func buildCoordMeta(
	asteroids []Coord,
	station Coord,
	coordBoundaryPredicate func(station Coord, ast Coord) bool,
) []CoordMeta {

	res := make([]CoordMeta, 0)
	for _, ast := range asteroids {
		if coordBoundaryPredicate(station, ast) {
			slope := getSlope(station, ast)
			dist := getDistance(station, ast)
			res = append(res, CoordMeta{ast, slope, dist})
		}
	}
	if len(res) == 0 {
		return []CoordMeta{}
	}
	return res
}

func createFiltered(res []CoordMeta) []CoordMeta {

	filtered := make([]CoordMeta, 0)
	filtered = append(filtered, res[0])

	lastSeen := res[0]

	for i := 1; i < len(res); i++ {
		if res[i].slope == nil {
			if lastSeen.slope != nil {
				filtered = append(filtered, res[i])
			}
			lastSeen = res[i]
			continue
		}
		if res[i].slope.Cmp(lastSeen.slope) != 0 {
			filtered = append(filtered, res[i])
		}
		lastSeen = res[i]
	}

	return filtered
}

func contains(arr []big.Rat, val big.Rat) bool {
	for _, item := range arr {
		if item.Cmp(&val) == 0 {
			return true
		}
	}
	return false
}
