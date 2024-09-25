package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strings"
)

func main() {
	input, err := os.ReadFile("./day2.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	intArr := createIntArrFromInput(input)
	intArr[1] = 12
	intArr[2] = 2

main:
	for i := 0; i < len(intArr); i += 4 {
		opCode := intArr[i]
		switch opCode {
		case 1:
			intArr[intArr[i+3]] = intArr[intArr[i+1]] + intArr[intArr[i+2]]
		case 2:
			intArr[intArr[i+3]] = intArr[intArr[i+1]] * intArr[intArr[i+2]]
		case 99:
			break main
		default:
			panic("Invalid opcode")
		}
	}
	fmt.Println(intArr[0])
}

func part2(input string) {
	for noun := 0; noun <= 99; noun++ {
		for verb := 0; verb <= 99; verb++ {
			intArr := createIntArrFromInput(input)
			intArr[1] = noun
			intArr[2] = verb

		main:
			for i := 0; i < len(intArr); i += 4 {
				opCode := intArr[i]
				switch opCode {
				case 1:
					intArr[intArr[i+3]] = intArr[intArr[i+1]] + intArr[intArr[i+2]]
				case 2:
					intArr[intArr[i+3]] = intArr[intArr[i+1]] * intArr[intArr[i+2]]
				case 99:
					break main
				default:
					panic(fmt.Sprintf("Invalid opcode: %d at index %d", opCode, i))
				}
			}
			if intArr[0] == 19690720 {
				fmt.Println(100*noun + verb)
				return
			}
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
