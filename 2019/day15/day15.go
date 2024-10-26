package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"strconv"
	"strings"
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
	comp          *IntcodeComp
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
	queue = append(queue, Path{Coord{0, 0}, initilizeComputer(input), 0, 0})
	visited := make(map[Coord]bool)

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		for nextDir := 1; nextDir <= 4; nextDir++ {
			if nextDir == curr.fromDirection {
				continue
			}
			computer := curr.comp.copy()
			output := computer.stepOne(nextDir)

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
	queue = append(queue, Path{Coord{0, 0}, initilizeComputer(input), 0, 0})
	visited := make(map[Coord]bool)
	var foundOxyGenSystem Coord

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		for nextDir := 1; nextDir <= 4; nextDir++ {
			if nextDir == curr.fromDirection {
				continue
			}
			computer := curr.comp.copy()
			output := computer.stepOne(nextDir)

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

type IntcodeComp struct {
	intArr        []int
	relativeBase  int
	programLength int
	pc            int
}

func (comp *IntcodeComp) copy() *IntcodeComp {
	newIntArr := make([]int, len(comp.intArr))
	copy(newIntArr, comp.intArr)
	return &IntcodeComp{newIntArr, comp.relativeBase, comp.programLength, comp.pc}
}

func (comp *IntcodeComp) stepOne(input int) int {
main:
	for comp.pc < comp.programLength {
		opCode := getOpCode(comp.intArr[comp.pc])
		paramModes := getParameterModes(comp.intArr[comp.pc])

		switch opCode {
		case 1:
			newVal := comp.getValueByMode(comp.pc+1, paramModes[0]) + comp.getValueByMode(comp.pc+2, paramModes[1])
			comp.writeValueByMode(comp.pc+3, paramModes[2], newVal)
			comp.pc += 4
		case 2:
			newVal := comp.getValueByMode(comp.pc+1, paramModes[0]) * comp.getValueByMode(comp.pc+2, paramModes[1])
			comp.writeValueByMode(comp.pc+3, paramModes[2], newVal)
			comp.pc += 4
		case 3:
			comp.writeValueByMode(comp.pc+1, paramModes[0], input)
			comp.pc += 2
		case 4:
			outputVal := comp.getValueByMode(comp.pc+1, paramModes[0])
			comp.pc += 2
			return outputVal
		case 5:
			if comp.getValueByMode(comp.pc+1, paramModes[0]) != 0 {
				comp.pc = comp.getValueByMode(comp.pc+2, paramModes[1])
			} else {
				comp.pc += 3
			}
		case 6:
			if comp.getValueByMode(comp.pc+1, paramModes[0]) == 0 {
				comp.pc = comp.getValueByMode(comp.pc+2, paramModes[1])
			} else {
				comp.pc += 3
			}
		case 7:
			if comp.getValueByMode(comp.pc+1, paramModes[0]) < comp.getValueByMode(comp.pc+2, paramModes[1]) {
				comp.writeValueByMode(comp.pc+3, paramModes[2], 1)
			} else {
				comp.writeValueByMode(comp.pc+3, paramModes[2], 0)
			}
			comp.pc += 4
		case 8:
			if comp.getValueByMode(comp.pc+1, paramModes[0]) == comp.getValueByMode(comp.pc+2, paramModes[1]) {
				comp.writeValueByMode(comp.pc+3, paramModes[2], 1)
			} else {
				comp.writeValueByMode(comp.pc+3, paramModes[2], 0)
			}
			comp.pc += 4
		case 9:
			comp.relativeBase = comp.relativeBase + comp.getValueByMode(comp.pc+1, paramModes[0])
			comp.pc += 2
		case 99:
			break main
		default:
			panic("Invalid opcode")
		}
	}
	return 0
}

func initilizeComputer(input string) *IntcodeComp {
	intArr := createIntArrFromInput(input)
	relativeBase := 0
	strArr := strings.Split(input, ",")
	programLength := len(strArr)

	return &IntcodeComp{intArr, relativeBase, programLength, 0}
}

func getOpCode(nbr int) int {
	str := strconv.Itoa(nbr)
	if len(str) > 1 {
		return util.ToInt(str[len(str)-2:])
	}
	return util.ToInt(str)
}

func getParameterModes(nbr int) map[int]int {
	mapping := map[int]int{
		0: 0,
		1: 0,
		2: 0,
	}
	str := strconv.Itoa(nbr)
	length := len(str)

	if length > 2 {
		mapping[0] = util.ToInt(string(str[length-3]))
	}
	if length > 3 {
		mapping[1] = util.ToInt(string(str[length-4]))
	}
	if length > 4 {
		mapping[2] = util.ToInt(string(str[length-5]))
	}

	return mapping
}

func (comp *IntcodeComp) writeValueByMode(index int, paramMode int, newValue int) {
	if paramMode == 2 { // Relative mode
		comp.intArr[(comp.relativeBase + comp.intArr[index])] = newValue
	} else { // Position mode
		comp.intArr[comp.intArr[index]] = newValue
	}
}

func (comp *IntcodeComp) getValueByMode(index int, paramMode int) int {
	if paramMode == 2 { // Relative mode
		return comp.intArr[(comp.relativeBase + comp.intArr[index])]
	} else if paramMode == 1 { // Immediate mode
		return comp.intArr[index]
	} else { // Position mode
		return comp.intArr[comp.intArr[index]]
	}
}

func createIntArrFromInput(input string) []int {
	strArr := strings.Split(input, ",")
	intArr := make([]int, 10_000)
	for i, val := range strArr {
		intArr[i] = util.ToInt(val)
	}
	return intArr
}
