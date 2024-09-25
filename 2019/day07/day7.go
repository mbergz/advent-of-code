package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"strconv"
	"strings"
	"sync"
)

func main() {
	input, err := os.ReadFile("./day7.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

// Not the most optional, Heap's algorithm better
func generatePermuations(size int, input []int, result *[][]int) {
	if size == 1 {
		copyOfInput := make([]int, len(input))
		copy(copyOfInput, input)
		*result = append(*result, copyOfInput)
	}

	for i := 0; i < size; i++ {
		generatePermuations(size-1, input, result)
		if size%2 == 0 {
			input[i], input[size-1] = input[size-1], input[i]
		} else {
			input[0], input[size-1] = input[size-1], input[0]
		}
	}
}

func part1(input string) {
	defer util.Timer()()

	basePhaseSettings := []int{0, 1, 2, 3, 4}
	var result [][]int
	generatePermuations(len(basePhaseSettings), basePhaseSettings, &result)

	var max float64 = 0

	for _, setting := range result {
		outputA := runIntCodeProgram(input, setting[0], 0)
		outputB := runIntCodeProgram(input, setting[1], outputA)
		outputC := runIntCodeProgram(input, setting[2], outputB)
		outputD := runIntCodeProgram(input, setting[3], outputC)
		outputE := runIntCodeProgram(input, setting[4], outputD)
		max = math.Max(float64(outputE), float64(max))
	}

	fmt.Println(int(max))
}

func part2(input string) {
	defer util.Timer()()

	basePhaseSettings := []int{5, 6, 7, 8, 9}
	var result [][]int
	generatePermuations(len(basePhaseSettings), basePhaseSettings, &result)

	var max float64 = 0
	var wg sync.WaitGroup

	chA := make(chan int)
	chB := make(chan int)
	chC := make(chan int)
	chD := make(chan int)
	chE := make(chan int)

	for _, setting := range result {
		wg.Add(5)

		go func() {
			defer wg.Done()
			runIntCodeProgramPart2(input, setting[0], chE, chA)
			wg.Add(1)
			go func() {
				defer wg.Done()
				lastValue := <-chE
				max = math.Max(float64(lastValue), float64(max))
			}()
		}()
		go func() {
			defer wg.Done()
			runIntCodeProgramPart2(input, setting[1], chA, chB)
		}()
		go func() {
			defer wg.Done()
			runIntCodeProgramPart2(input, setting[2], chB, chC)
		}()
		go func() {
			defer wg.Done()
			runIntCodeProgramPart2(input, setting[3], chC, chD)
		}()
		go func() {
			defer wg.Done()
			runIntCodeProgramPart2(input, setting[4], chD, chE)
		}()

		go func() {
			chE <- 0
		}()

		wg.Wait()
	}
	fmt.Println(int(max))
}

func runIntCodeProgram(input string, phaseSetting int, inputSignal int) int {
	intArr := createIntArrFromInput(input)
	firstRun := true

main:
	for i := 0; i < len(intArr); {
		opCode := getOpCode(intArr[i])
		paramModes := getParameterModes(intArr[i])

		switch opCode {
		case 1:
			intArr[intArr[i+3]] = getValueByMode(intArr, i+1, paramModes[0]) + getValueByMode(intArr, i+2, paramModes[1])
			i += 4
		case 2:
			intArr[intArr[i+3]] = getValueByMode(intArr, i+1, paramModes[0]) * getValueByMode(intArr, i+2, paramModes[1])
			i += 4
		case 3:
			if firstRun {
				intArr[intArr[i+1]] = phaseSetting
				firstRun = false
			} else {
				intArr[intArr[i+1]] = inputSignal
			}
			i += 2
		case 4:
			outVal := getValueByMode(intArr, i+1, paramModes[0])
			//fmt.Printf("Output: %d\n", outVal)
			return outVal
			//i += 2
		case 5:
			if getValueByMode(intArr, i+1, paramModes[0]) > 0 {
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
				intArr[intArr[i+3]] = 1
			} else {
				intArr[intArr[i+3]] = 0
			}
			i += 4
		case 8:
			if getValueByMode(intArr, i+1, paramModes[0]) == getValueByMode(intArr, i+2, paramModes[1]) {
				intArr[intArr[i+3]] = 1
			} else {
				intArr[intArr[i+3]] = 0
			}
			i += 4
		case 99:
			break main
		default:
			panic("Invalid opcode")
		}
	}
	panic("Should not happen!")
}

func runIntCodeProgramPart2(input string, phaseSetting int, inputChannel chan int, outputChannel chan int) {
	intArr := createIntArrFromInput(input)
	firstRun := true

main:
	for i := 0; i < len(intArr); {
		opCode := getOpCode(intArr[i])
		paramModes := getParameterModes(intArr[i])

		switch opCode {
		case 1:
			intArr[intArr[i+3]] = getValueByMode(intArr, i+1, paramModes[0]) + getValueByMode(intArr, i+2, paramModes[1])
			i += 4
		case 2:
			intArr[intArr[i+3]] = getValueByMode(intArr, i+1, paramModes[0]) * getValueByMode(intArr, i+2, paramModes[1])
			i += 4
		case 3:
			if firstRun {
				intArr[intArr[i+1]] = phaseSetting
				firstRun = false
			} else {
				var receivedInput int = <-inputChannel
				intArr[intArr[i+1]] = receivedInput
			}
			i += 2
		case 4:
			outVal := getValueByMode(intArr, i+1, paramModes[0])
			outputChannel <- outVal
			i += 2
		case 5:
			if getValueByMode(intArr, i+1, paramModes[0]) > 0 {
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
				intArr[intArr[i+3]] = 1
			} else {
				intArr[intArr[i+3]] = 0
			}
			i += 4
		case 8:
			if getValueByMode(intArr, i+1, paramModes[0]) == getValueByMode(intArr, i+2, paramModes[1]) {
				intArr[intArr[i+3]] = 1
			} else {
				intArr[intArr[i+3]] = 0
			}
			i += 4
		case 99:
			break main
		default:
			panic("Invalid opcode")
		}
	}
}

func createIntArrFromInput(input string) []int {
	strArr := strings.Split(input, ",")
	intArr := make([]int, len(strArr))
	for i, val := range strArr {
		intArr[i] = util.ToInt(val)
	}
	return intArr
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

func getValueByMode(arr []int, index int, paramMode int) int {
	if paramMode == 1 {
		return arr[index]
	} else { // Must be 0
		return arr[arr[index]]
	}
}
