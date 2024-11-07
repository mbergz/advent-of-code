package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strings"
)

var foundPaths [][]Node

type Coord struct {
	x int
	y int
}

type Node struct {
	coord         Coord
	direction     RelativeDirection
	fromDirection AbsoluteDirection
}

type AbsoluteDirection int

const (
	UP AbsoluteDirection = iota
	RIGHT
	DOWN
	LEFT
)

type RelativeDirection int

const (
	R_FORWARD RelativeDirection = iota
	R_LEFT
	R_RIGHT
)

var forwardFrom = map[AbsoluteDirection]AbsoluteDirection{
	UP:    DOWN,
	RIGHT: LEFT,
	DOWN:  UP,
	LEFT:  RIGHT,
}

var turnLeftFrom = map[AbsoluteDirection]AbsoluteDirection{
	UP:    RIGHT,
	RIGHT: DOWN,
	DOWN:  LEFT,
	LEFT:  UP,
}

var turnRightFrom = map[AbsoluteDirection]AbsoluteDirection{
	UP:    LEFT,
	RIGHT: UP,
	DOWN:  RIGHT,
	LEFT:  DOWN,
}

func main() {
	input, err := os.ReadFile("./day17.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	comp := intcodecomputer.InitilizeComputer(input)
	result := comp.Run(make([]int, 0))

	var scaffoldMap [][]int = createScaffoldMap(result)

	alignmentParameters := 0
	for i := 1; i < len(scaffoldMap)-1; i++ {
		for j := 1; j < len(scaffoldMap[i])-1; j++ {
			if string(scaffoldMap[i][j]) != "#" {
				continue
			}

			if string(scaffoldMap[i-1][j]) == "#" &&
				string(scaffoldMap[i+1][j]) == "#" &&
				string(scaffoldMap[i][j-1]) == "#" &&
				string(scaffoldMap[i][j+1]) == "#" {
				alignmentParameters += i * j
			}

		}
	}
	println(alignmentParameters)
}

func part2(input string) {
	defer util.Timer()()

	comp := intcodecomputer.InitilizeComputer(input)
	result := comp.Run(make([]int, 0))

	var scaffoldMap [][]int = createScaffoldMap(result)

	totalVisited := 0
	var startCoord Coord
	for iRow, row := range scaffoldMap {
		for iColumn, column := range row {
			if string(column) == "#" {
				totalVisited++
			}
			if string(column) == "^" {
				startCoord = Coord{iColumn, iRow}
			}
		}
	}
	fmt.Println("Total nbr of # is", totalVisited)
	fmt.Println(startCoord)

	visitedMap := make(map[Node]bool)
	startNode := Node{
		startCoord,
		R_FORWARD,
		DOWN,
	}

	path := make([]Node, 0)
	path = append(path, startNode)

	runDfs(path, scaffoldMap, visitedMap, totalVisited)

	longestPath := getLongestPath()
	longestPath = longestPath[1:]

	var sb strings.Builder
	for _, path := range longestPath {
		switch path.direction {
		case R_FORWARD:
			sb.WriteString(".")
		case R_LEFT:
			sb.WriteString("L")
		case R_RIGHT:
			sb.WriteString("R")
		}
	}
	fmt.Println(sb.String())

	// ABACABACBC
	// A => R,4,L,12,L8,R,4
	// B => L,8,R,10,R,10,R,6
	// C => R,4,R,10,L,12

	inputToComp := convertToAscii("A,B,A,C,A,B,A,C,B,C\nR,4,L,12,L,8,R,4\nL,8,R,10,R,10,R,6\nR,4,R,10,L,12\nn\n")

	modifiedInput := "2" + input[1:]
	comp = intcodecomputer.InitilizeComputer(modifiedInput)
	result = comp.Run(inputToComp)
	fmt.Println(result[len(result)-1])
}

func createScaffoldMap(result []int) [][]int {
	var scaffoldMap [][]int

	row := 0
	column := 0
	for _, res := range result {
		asciiStr := string(res)
		if asciiStr == "\n" {
			fmt.Println()
			row++
			column = 0
		} else {
			if row >= len(scaffoldMap) {
				scaffoldMap = append(scaffoldMap, []int{})
			}
			scaffoldMap[row] = append(scaffoldMap[row], res)
			column++
			fmt.Print(asciiStr)
		}
	}

	return scaffoldMap
}

func convertToAscii(input string) []int {
	res := make([]int, 0)
	for _, ch := range input {
		res = append(res, int(ch))
	}
	return res
}

func getLongestPath() []Node {
	var longestPath []Node
	longest := 0
	for _, path := range foundPaths {
		if len(path) > longest {
			longestPath = path
			longest = len(path)
		}
	}
	return longestPath
}

func runDfs(current []Node, graph [][]int, visited map[Node]bool, totalVisited int) {
	lastElem := current[len(current)-1]
	if _, ok := visited[lastElem]; !ok {
		visited[lastElem] = true
	}

	// loop forward until stop
	forwardCoord := goForward(lastElem.coord, lastElem.fromDirection)
	for coordIsScaffold(forwardCoord, graph) {
		newNode := Node{
			forwardCoord,
			R_FORWARD,
			lastElem.fromDirection, // Keep same from direction
		}
		current = append(current, newNode)
		forwardCoord = goForward(newNode.coord, newNode.fromDirection)
	}

	lastElem = current[len(current)-1]

	for _, value := range getNext(lastElem, graph) {
		current = append(current, value)
		runDfs(current, graph, visited, totalVisited)
	}
	if len(current) >= totalVisited {
		foundPaths = append(foundPaths, current)
	}
}

func getNext(lastElem Node, graph [][]int) []Node {
	res := make([]Node, 0)

	leftCoord, newDirection := turn90Left(lastElem.coord, lastElem.fromDirection)
	if coordIsScaffold(leftCoord, graph) {
		res = append(res, Node{leftCoord, R_LEFT, forwardFrom[newDirection]})
	}

	rightCoord, newDirection := turn90Right(lastElem.coord, lastElem.fromDirection)
	if coordIsScaffold(rightCoord, graph) {
		res = append(res, Node{rightCoord, R_RIGHT, forwardFrom[newDirection]})
	}

	return res
}

func coordIsScaffold(coord Coord, graph [][]int) bool {
	minX, minY := 0, 0
	maxY := len(graph) - 1
	maxX := len(graph[0]) - 1

	if coord.x >= minX && coord.x <= maxX &&
		coord.y >= minY && coord.y <= maxY {
		return string(graph[coord.y][coord.x]) == "#"
	}
	return false
}

func goForward(current Coord, fromDirection AbsoluteDirection) Coord {
	newDirection := forwardFrom[fromDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord
}

func turn90Left(current Coord, fromDirection AbsoluteDirection) (Coord, AbsoluteDirection) {
	newDirection := turnLeftFrom[fromDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord, newDirection
}

func turn90Right(current Coord, fromDirection AbsoluteDirection) (Coord, AbsoluteDirection) {
	newDirection := turnRightFrom[fromDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord, newDirection
}

func calcNewCoord(current Coord, newDirection AbsoluteDirection) Coord {
	switch newDirection {
	case UP:
		return Coord{current.x, current.y - 1}
	case RIGHT:
		return Coord{current.x + 1, current.y}
	case DOWN:
		return Coord{current.x, current.y + 1}
	case LEFT:
		return Coord{current.x - 1, current.y}
	}
	return Coord{}
}
