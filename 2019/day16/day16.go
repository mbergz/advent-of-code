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
		newNbrs := make([]int, 0)

		for itr := 1; itr <= len(nbrs); itr++ {
			pattern := createPattern(itr, len(nbrs))

			inputDigits := make([]int, 0)
			for i := 0; i < len(nbrs); i++ {
				newNbr := nbrs[i] * pattern[i]
				inputDigits = append(inputDigits, leastSignificantBit(newNbr))
			}
			var sum int = 0
			for _, inDigit := range inputDigits {
				sum += int(inDigit)
			}
			newNbrs = append(newNbrs, util.AbsInt(leastSignificantBit(sum)))
		}

		nbrs = newNbrs
	}

	for _, nbr := range nbrs {
		fmt.Print(nbr)
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

func createPattern(iteration int, maxLength int) []int {
	modifiedBase := make([]int, 0)
	for _, baseNbr := range basePattern {
		for i := 0; i < iteration; i++ {
			modifiedBase = append(modifiedBase, baseNbr)
		}
	}

	resultArr := make([]int, 0)

	for len(resultArr) != maxLength+1 {
		diff := (maxLength + 1) - len(resultArr)

		if diff < len(modifiedBase) {
			resultArr = append(resultArr, modifiedBase[:diff]...)
		} else {
			resultArr = append(resultArr, modifiedBase...)
		}
	}

	return resultArr[1:]
}

func part2(input string) {
	defer util.Timer()()

}
