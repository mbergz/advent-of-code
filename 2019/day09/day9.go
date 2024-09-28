package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strconv"
	"strings"
)

var (
	relativeBase  int
	programLength int
)

func main() {
	input, err := os.ReadFile("./day9.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	intCodeComputer(input)
}

func part2(input string) {
	defer util.Timer()()

	intCodeComputer(input)
}

func intCodeComputer(input string) {
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
			var userInput int
			fmt.Println("Input instruction:")
			_, err := fmt.Scanln(&userInput)
			if err != nil {
				panic(err)
			}
			writeValueByMode(intArr, i+1, paramModes[0], userInput)
			i += 2
		case 4:
			fmt.Printf("%d\n", getValueByMode(intArr, i+1, paramModes[0]))
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
