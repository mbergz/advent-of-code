package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	input, err := os.ReadFile("./day4.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	fromRange := util.ToInt(strings.Split(strings.TrimSpace(input), "-")[0])
	toRange := util.ToInt(strings.Split(strings.TrimSpace(input), "-")[1])

	possibleNumbers := 0
	for i := fromRange + 1; i < toRange; i++ {
		if isIncreasing(i) && hasDouble(i) {
			possibleNumbers++
		}
	}
	fmt.Println(possibleNumbers)
}

func part2(input string) {
	fromRange := util.ToInt(strings.Split(strings.TrimSpace(input), "-")[0])
	toRange := util.ToInt(strings.Split(strings.TrimSpace(input), "-")[1])

	possibleNumbers := 0
	for i := fromRange + 1; i < toRange; i++ {
		if isIncreasing(i) && hasDoublePart2(i) {
			possibleNumbers++
		}
	}
	fmt.Println(possibleNumbers)
}

func hasDouble(nbr int) bool {
	occurences := make(map[int]int)
	converted := strconv.Itoa(nbr)

	for i := 0; i < len(converted); i++ {
		nbr := util.ToInt(string(converted[i]))
		occurences[nbr] = occurences[nbr] + 1
	}
	for _, value := range occurences {
		if value > 1 {
			return true
		}
	}
	return false
}

func hasDoublePart2(nbr int) bool {
	occurences := make(map[int]int)
	converted := strconv.Itoa(nbr)

	for i := 0; i < len(converted); i++ {
		nbr := util.ToInt(string(converted[i]))
		occurences[nbr] = occurences[nbr] + 1
	}

	for _, value := range occurences {
		if value == 2 {
			return true
		}
	}
	return false
}

func isIncreasing(nbr int) bool {
	converted := strconv.Itoa(nbr)

	for i := 1; i < len(converted); i++ {
		if util.ToInt(string(converted[i])) < util.ToInt(string(converted[i-1])) {
			return false
		}
	}
	return true
}
