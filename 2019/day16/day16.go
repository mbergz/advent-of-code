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
				newNbr := nbrs[i] * getPatternModifier(itr, i)
				sum += leastSignificantBit(newNbr)
			}
			nbrs[itr-1] = util.AbsInt(leastSignificantBit(sum))
		}
	}

	for _, nbr := range nbrs {
		fmt.Print(nbr)
	}
	fmt.Println()
}

func part2(input string) {
	defer util.Timer()()

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
