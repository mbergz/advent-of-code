package main

import (
	"advent-of-code_2019/intcodecomputer"
	"fmt"
	"os"
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
	comp := intcodecomputer.InitilizeComputer(input)
	comp.GetIntArr()[1] = 12
	comp.GetIntArr()[2] = 2

	comp.RunInteractiveMode()

	fmt.Println(comp.GetIntArr()[0])
}

func part2(input string) {
	for noun := 0; noun <= 99; noun++ {
		for verb := 0; verb <= 99; verb++ {

			comp := intcodecomputer.InitilizeComputer(input)
			comp.GetIntArr()[1] = noun
			comp.GetIntArr()[2] = verb

			comp.RunInteractiveMode()

			if comp.GetIntArr()[0] == 19690720 {
				fmt.Println(100*noun + verb)
				return
			}
		}
	}
}
