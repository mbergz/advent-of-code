package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"os"
)

func main() {
	input, err := os.ReadFile("./day5.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	intcodecomputer.InitilizeComputer(input).RunInteractiveMode()
}

func part2(input string) {
	defer util.Timer()()

	intcodecomputer.InitilizeComputer(input).RunInteractiveMode()
}
