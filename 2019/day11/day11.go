package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"strconv"
	"strings"
)

type Direction int

const (
	UP Direction = iota
	RIGHT
	DOWN
	LEFT
)

var turnLeft = map[Direction]Direction{
	UP:    LEFT,
	RIGHT: UP,
	DOWN:  RIGHT,
	LEFT:  DOWN,
}

var turnRight = map[Direction]Direction{
	UP:    RIGHT,
	RIGHT: DOWN,
	DOWN:  LEFT,
	LEFT:  UP,
}

type Coord struct {
	x int
	y int
}

var (
	relativeBase  int
	programLength int
)

func main() {
	input, err := os.ReadFile("./day11.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	inputChannel := make(chan int)
	outputChannel := make(chan int)
	quit := make(chan bool)

	totalCount := 0

	go func() {
		intCodeComputer(input, inputChannel, outputChannel)
		fmt.Println(totalCount)
		quit <- true
	}()

	var panels map[Coord]int = make(map[Coord]int, 0)
	current := Coord{0, 0}
	direction := UP

	for {
		select {
		case <-quit:
			return
		case inputChannel <- panels[current]:
			// Color
			output1 := <-outputChannel

			_, exists := panels[current]
			if !exists {
				totalCount++ // New squre
			}
			if output1 == 0 { // Paint black
				panels[current] = 0
			} else if output1 == 1 { // Paint white
				panels[current] = 1
			}
			// Direction
			output2 := <-outputChannel

			if output2 == 0 {
				current, direction = turn90Left(current, direction)
			} else if output2 == 1 {
				current, direction = turn90Right(current, direction)
			}
		}
	}
}

func part2(input string) {
	defer util.Timer()()

	inputChannel := make(chan int)
	outputChannel := make(chan int)
	quit := make(chan bool)

	var panels map[Coord]int = make(map[Coord]int, 0)
	current := Coord{0, 0}
	panels[current] = 1
	direction := UP

	go func() {
		intCodeComputer(input, inputChannel, outputChannel)
		quit <- true
	}()

mainLoop:
	for {
		select {
		case <-quit:
			break mainLoop
		case inputChannel <- panels[current]:
			// Color
			output1 := <-outputChannel

			if output1 == 0 { // Paint black
				panels[current] = 0
			} else if output1 == 1 { // Paint white
				panels[current] = 1
			}
			// Direction
			output2 := <-outputChannel

			if output2 == 0 {
				current, direction = turn90Left(current, direction)
			} else if output2 == 1 {
				current, direction = turn90Right(current, direction)
			}
		}
	}

	minX, minY := math.MaxInt, math.MaxInt
	maxX, maxY := math.MinInt, math.MinInt

	for key, _ := range panels {
		minX = min(minX, key.x)
		minY = min(minY, key.y)
		maxX = max(maxX, key.x)
		maxY = max(maxY, key.y)
	}

	for i := minY - 1; i <= maxY+1; i++ {
		for j := minX - 1; j <= maxX+1; j++ {
			if value, ok := panels[Coord{j, i}]; ok && value == 1 {
				fmt.Print("# ")
			} else {
				fmt.Print(". ")
			}
		}
		fmt.Print("\n")
	}
}

func turn90Left(current Coord, currentDirection Direction) (Coord, Direction) {
	newDirection := turnLeft[currentDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord, newDirection
}

func turn90Right(current Coord, currentDirection Direction) (Coord, Direction) {
	newDirection := turnRight[currentDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord, newDirection
}

func calcNewCoord(current Coord, newDirection Direction) Coord {
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

func intCodeComputer(input string, inputChannel chan int, outputChannel chan int) {
	intArr := createIntArrFromInput(input)
	relativeBase = 0

main:
	for i := 0; i < programLength; {
		opCode := getOpCode(intArr[i])
		paramModes := getParameterModes(intArr[i])

		switch opCode {
		case 1:
			newVal := getValueByMode(intArr, i+1, paramModes[0]) + getValueByMode(intArr, i+2, paramModes[1])
			writeValueByMode(intArr, i+3, paramModes[2], newVal)
			i += 4
		case 2:
			newVal := getValueByMode(intArr, i+1, paramModes[0]) * getValueByMode(intArr, i+2, paramModes[1])
			writeValueByMode(intArr, i+3, paramModes[2], newVal)
			i += 4
		case 3:
			var receivedInput int = <-inputChannel
			writeValueByMode(intArr, i+1, paramModes[0], receivedInput)
			i += 2
		case 4:
			outVal := getValueByMode(intArr, i+1, paramModes[0])
			outputChannel <- outVal
			i += 2
		case 5:
			if getValueByMode(intArr, i+1, paramModes[0]) != 0 {
				i = getValueByMode(intArr, i+2, paramModes[1])
			} else {
				i += 3
			}
		case 6:
			if getValueByMode(intArr, i+1, paramModes[0]) == 0 {
				i = getValueByMode(intArr, i+2, paramModes[1])
			} else {
				i += 3
			}
		case 7:
			if getValueByMode(intArr, i+1, paramModes[0]) < getValueByMode(intArr, i+2, paramModes[1]) {
				writeValueByMode(intArr, i+3, paramModes[2], 1)
			} else {
				writeValueByMode(intArr, i+3, paramModes[2], 0)
			}
			i += 4
		case 8:
			if getValueByMode(intArr, i+1, paramModes[0]) == getValueByMode(intArr, i+2, paramModes[1]) {
				writeValueByMode(intArr, i+3, paramModes[2], 1)
			} else {
				writeValueByMode(intArr, i+3, paramModes[2], 0)
			}
			i += 4
		case 9:
			relativeBase = relativeBase + getValueByMode(intArr, i+1, paramModes[0])
			i += 2
		case 99:
			break main
		default:
			panic("Invalid opcode")
		}
	}
	close(outputChannel)
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

func writeValueByMode(arr []int, index int, paramMode int, newValue int) {
	if paramMode == 2 { // Relative mode
		arr[(relativeBase + arr[index])] = newValue
	} else { // Position mode
		arr[arr[index]] = newValue
	}
}

func getValueByMode(arr []int, index int, paramMode int) int {
	if paramMode == 2 { // Relative mode
		return arr[(relativeBase + arr[index])]
	} else if paramMode == 1 { // Immediate mode
		return arr[index]
	} else { // Position mode
		return arr[arr[index]]
	}
}

func createIntArrFromInput(input string) []int {
	strArr := strings.Split(input, ",")
	intArr := make([]int, 10_000)
	programLength = len(strArr)
	for i, val := range strArr {
		intArr[i] = util.ToInt(val)
	}
	return intArr
}
