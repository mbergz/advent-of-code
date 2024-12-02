package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"fmt"
	"os"
)

func main() {
	input, err := os.ReadFile("./day19.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	points := 0
	for i := 0; i < 50; i++ {
		for j := 0; j < 50; j++ {
			res := intcodecomputer.InitilizeComputer(input).Run([]int{j, i})
			if res[0] == 1 {
				fmt.Print("# ")
				points += 1
			} else {
				fmt.Print(". ")
			}
		}
		fmt.Println()
	}
	fmt.Println(points)
}

func part2(input string) {
	defer util.Timer()()

}
