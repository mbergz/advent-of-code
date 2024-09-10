package main

import (
	"fmt"
	"math"
	"os"
	"strings"

	"advent-of-code_2019/util"
)

func main() {
	input, err := os.ReadFile("./day1.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	total := 0
	for _, line := range strings.Split(input, "\n") {
		total += calculate(util.ToInt(strings.TrimSpace(line)))
	}
	fmt.Println(total)
}

func part2(input string) {
	total := 0
	for _, line := range strings.Split(input, "\n") {
		total += recursiveCalculate(util.ToInt(strings.TrimSpace(line)))
	}
	fmt.Println(total)
}

func recursiveCalculate(value int) int {
	newValue := calculate(value)
	if newValue <= 0 {
		return 0
	}
	return newValue + recursiveCalculate(newValue)
}

func calculate(value int) int {
	return int(math.Floor(float64(value)/float64(3)) - 2)
}
