package intcodecomputer

import (
	"advent-of-code_2019/util"
	"fmt"
	"strconv"
	"strings"
)

type IntcodeComp struct {
	intArr        []int
	relativeBase  int
	programLength int
	pc            int
}

func (comp *IntcodeComp) GetIntArr() []int {
	return comp.intArr
}

func (comp *IntcodeComp) Copy() *IntcodeComp {
	newIntArr := make([]int, len(comp.intArr))
	copy(newIntArr, comp.intArr)
	return &IntcodeComp{newIntArr, comp.relativeBase, comp.programLength, comp.pc}
}

func (comp *IntcodeComp) RunInteractiveMode() {
	inputFn := func() int {
		var userInput int
		fmt.Println("Input instruction:")
		_, err := fmt.Scanln(&userInput)
		if err != nil {
			panic(err)
		}
		return userInput
	}

	outputFn := func(next int) {
		fmt.Println(next)
	}

	comp.run(inputFn, outputFn, false)
}

func (comp *IntcodeComp) StepOne(input int) int {
	inputFn := func() int {
		return input
	}

	var output int
	outputFn := func(next int) {
		output = next
	}

	comp.run(inputFn, outputFn, true)
	return output
}

func (comp *IntcodeComp) Run(input []int) []int {
	inputIndex := 0
	inputFn := func() int {
		val := input[inputIndex]
		inputIndex++
		return val
	}

	outputArr := []int{}
	outputFn := func(next int) {
		outputArr = append(outputArr, next)
	}
	comp.run(inputFn, outputFn, false)
	return outputArr
}

func (comp *IntcodeComp) RunChannels(inputChannel chan int, outputChannel chan int) {
	inputFn := func() int {
		return <-inputChannel
	}
	outputFn := func(next int) {
		outputChannel <- next
	}
	comp.run(inputFn, outputFn, false)
}

func (comp *IntcodeComp) run(input func() int, output func(int), stop bool) {
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
			comp.writeValueByMode(comp.pc+1, paramModes[0], input())
			comp.pc += 2
		case 4:
			outputVal := comp.getValueByMode(comp.pc+1, paramModes[0])
			output(outputVal)
			comp.pc += 2
			if stop {
				return
			}
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
}

func InitilizeComputer(input string) *IntcodeComp {
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
