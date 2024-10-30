package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
)

var (
	basePattern []int = []int{0, 1, 0, -1}
)

func main() {
	input, err := os.ReadFile("./day16.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	nbrs := make([]int, 0)
	for _, char := range input {
		nbrs = append(nbrs, int(util.ToInt(string(char))))
	}

	for phase := 1; phase <= 100; phase++ {
		for itr := 1; itr <= len(nbrs); itr++ {
			sum := 0
			for i := itr - 1; i < len(nbrs); i++ {
				sum += nbrs[i] * getPatternModifier(itr, i)
			}
			nbrs[itr-1] = util.AbsInt(leastSignificantBit(sum))
		}
	}

	for i := 0; i < 8; i++ {
		fmt.Print(nbrs[i])
	}
	fmt.Println()
}

func convertSliceToNumber(digits []int) int {
	number := 0
	for _, digit := range digits {
		number = number*10 + digit
	}
	return number
}

func part2(input string) {
	defer util.Timer()()

	origNbrs := make([]int, 0)
	for _, char := range input {
		origNbrs = append(origNbrs, int(util.ToInt(string(char))))
	}

	nbrs := make([]int, 0, len(origNbrs)*10_000)
	for i := 0; i < 10_000; i++ {
		nbrs = append(nbrs, origNbrs...)
	}

	offset := convertSliceToNumber(nbrs[:7])
	nbrs = nbrs[offset:]

	for phase := 1; phase <= 100; phase++ {

		totalSum := 0
		// Pattern modifier is always 1 becasue of offset (thx reddit)
		for i := 0; i < len(nbrs); i++ {
			totalSum += nbrs[i]
		}

		offsetSum := 0
		last := 0
		for itr := 0; itr < len(nbrs); itr++ {
			offsetSum += last
			last = nbrs[itr]
			nbrs[itr] = util.AbsInt(leastSignificantBit(totalSum - offsetSum))
		}
	}

	for i := 0; i < 8; i++ {
		fmt.Print(nbrs[i])
	}
	fmt.Println()
}

func leastSignificantBit(nbr int) int {
	if nbr > 0 {
		return nbr % 10
	} else {
		return nbr % -10
	}
}

func getPatternModifier(iteration int, index int) int {
	pos := (index + 1) / iteration
	return basePattern[pos%4]
}
