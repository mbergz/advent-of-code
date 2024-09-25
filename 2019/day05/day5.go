package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	input, err := os.ReadFile("./day5.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
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

func part1(input string) {
	defer util.Timer()()

	intArr := createIntArrFromInput(input)

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
			intArr[intArr[i+1]] = 1
			i += 2
		case 4:
			fmt.Printf("%d\n", getValueByMode(intArr, i+1, paramModes[0]))
			i += 2
		case 99:
			break main
		default:
			panic("Invalid opcode")
		}
	}
}

func part2(input string) {
	defer util.Timer()()

	intArr := createIntArrFromInput(input)

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
			var userInput int
			fmt.Println("Input instruction:")
			_, err := fmt.Scanf("%d", &userInput)
			if err != nil {
				panic(err)
			}
			intArr[intArr[i+1]] = userInput
			i += 2
		case 4:
			fmt.Printf("%d\n", getValueByMode(intArr, i+1, paramModes[0]))
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
