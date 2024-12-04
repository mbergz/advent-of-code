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

	currY := 11
	currX := 9
	for {
		currY++
		prevX := currX
		nbrLeft := getLeft(currX, currY, input)
		nbrRight := getRight(currX, currY, input)
		total := nbrLeft + 1 + nbrRight
		if nbrLeft > nbrRight {
			currX -= (nbrLeft - nbrRight) / 2
		} else {
			currX += (nbrRight - nbrLeft) / 2
		}
		if total > 100 {
			beamMaxRightX := prevX + nbrRight
			if checkSquare(beamMaxRightX, currY, input) {
				fmt.Println((beamMaxRightX-99)*10000 + currY)
				return
			}
		}
	}

}

func checkSquare(maxX int, y int, input string) bool {
	return intcodecomputer.InitilizeComputer(input).Run([]int{maxX - 99, y + 99})[0] == 1
}

func getLeft(x int, y int, input string) int {
	return getCount(x, y, input, -1)
}
func getRight(x int, y int, input string) int {
	return getCount(x, y, input, +1)
}

func getCount(x int, y int, input string, direction int) int {
	count := 0
	for {
		x += direction
		if intcodecomputer.InitilizeComputer(input).Run([]int{x, y})[0] == 1 {
			count++
		} else {
			return count
		}
	}
}
