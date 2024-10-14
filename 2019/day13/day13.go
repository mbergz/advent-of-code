package main

import (
	"advent-of-code_2019/util"
	"bytes"
	"fmt"
	"io"
	"math"
	"os"
	"strconv"
	"strings"
	"sync"
	"time"
)

type TileType int

const (
	EMPTY TileType = iota
	WALL
	BLOCK
	H_PADDLE
	BALL
)

type Coord struct {
	x int
	y int
}

var (
	maxX         int
	maxY         int
	currentScore int
)

func main() {
	input, err := os.ReadFile("./day13.txt")
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

	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		defer wg.Done()
		defer close(inputChannel)

		intCodeComputer(input, inputChannel, outputChannel)
	}()

	grid := make(map[Coord]TileType, 0)

	go func() {
		defer wg.Done()

		count := 0
		var x int
		var y int
		var tileId TileType

		for output := range outputChannel {
			count++
			if count == 1 {
				x = output
			}
			if count == 2 {
				y = output
			}
			if count == 3 {
				tileId = TileType(output)
				if EMPTY != tileId {
					grid[Coord{x, y}] = tileId
				}
				count = 0
			}
		}
	}()

	wg.Wait()

	blockCount := 0
	for _, value := range grid {
		if value == BLOCK {
			blockCount++
		}
	}
	fmt.Println(blockCount)
}

func part2(input string) {
	defer util.Timer()()

	inputChannel := make(chan int)
	outputChannel := make(chan int)

	var wg sync.WaitGroup
	wg.Add(4)

	go func() {
		defer wg.Done()

		modifiedInput := []rune(input)
		modifiedInput[0] = '2'

		intCodeComputer(string(modifiedInput), inputChannel, outputChannel)
	}()

	gameActive := false
	quit := false
	grid := make(map[Coord]TileType, 0)

	go func() {
		defer wg.Done()

		count := 0
		var x int
		var y int
		var tileId TileType

		for output := range outputChannel {
			count++
			if count == 1 {
				x = output
			}
			if count == 2 {
				y = output
			}
			if count == 3 {
				if x == -1 && y == 0 {
					if !gameActive { // Init
						maxX = math.MinInt
						maxY = math.MinInt

						for key, _ := range grid {
							maxX = max(maxX, key.x)
							maxY = max(maxY, key.y)
						}

						gameActive = true
					}

					currentScore = output
				} else {
					tileId = TileType(output)
					grid[Coord{x, y}] = tileId
				}
				count = 0
			}
		}
	}()

	go func() {
		defer wg.Done()

		var b []byte = make([]byte, 3)

		for {
			os.Stdin.Read(b)

			if string(b[0]) == "q" {
				quit = true
				break
			}
		}
	}()

	go func() {
		defer wg.Done()

		for {
			if quit {
				break
			}

			if gameActive {
				printGameState(grid)
			}

			xBall := 0
			xPaddle := 0

			for key, value := range grid {
				if value == BALL {
					xBall = key.x
				}
				if value == H_PADDLE {
					xPaddle = key.x
				}
			}

			if xBall == xPaddle {
				inputChannel <- 0
			} else if xBall > xPaddle {
				inputChannel <- 1
			} else {
				inputChannel <- -1
			}

			time.Sleep(10 * time.Millisecond)
		}
	}()

	wg.Wait()
}

func printGameState(grid map[Coord]TileType) {
	var buf bytes.Buffer

	buf.WriteString("Score: " + strconv.Itoa(currentScore) + "\n\n")

	for i := 0; i <= maxY+1; i++ {
		for j := 0; j <= maxX+1; j++ {
			val, exists := grid[Coord{j, i}]
			if exists {
				if val == EMPTY {
					buf.WriteString(" ")
				}
				if val == WALL {
					buf.WriteString("W")
				}
				if val == BLOCK {
					buf.WriteString("B")
				}
				if val == H_PADDLE {
					buf.WriteString("_")
				}
				if val == BALL {
					buf.WriteString("o")
				}
			} else {
				buf.WriteString(" ")
			}
		}
		buf.WriteByte('\n')
	}

	io.Copy(os.Stdout, bytes.NewBuffer(buf.Bytes()))
}

var (
	relativeBase  int
	programLength int
)

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
