package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"sync"
)

func main() {
	input, err := os.ReadFile("./day7.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

// Not the most optional, Heap's algorithm better
func generatePermuations(size int, input []int, result *[][]int) {
	if size == 1 {
		copyOfInput := make([]int, len(input))
		copy(copyOfInput, input)
		*result = append(*result, copyOfInput)
	}

	for i := 0; i < size; i++ {
		generatePermuations(size-1, input, result)
		if size%2 == 0 {
			input[i], input[size-1] = input[size-1], input[i]
		} else {
			input[0], input[size-1] = input[size-1], input[0]
		}
	}
}

func part1(input string) {
	defer util.Timer()()

	basePhaseSettings := []int{0, 1, 2, 3, 4}
	var result [][]int
	generatePermuations(len(basePhaseSettings), basePhaseSettings, &result)

	var max float64 = 0

	for _, setting := range result {

		outputA := intcodecomputer.InitilizeComputer(input).Run([]int{setting[0], 0})[0]
		outputB := intcodecomputer.InitilizeComputer(input).Run([]int{setting[1], outputA})[0]
		outputC := intcodecomputer.InitilizeComputer(input).Run([]int{setting[2], outputB})[0]
		outputD := intcodecomputer.InitilizeComputer(input).Run([]int{setting[3], outputC})[0]
		outputE := intcodecomputer.InitilizeComputer(input).Run([]int{setting[4], outputD})[0]
		max = math.Max(float64(outputE), float64(max))
	}

	fmt.Println(int(max))
}

func part2(input string) {
	defer util.Timer()()

	basePhaseSettings := []int{5, 6, 7, 8, 9}
	var result [][]int
	generatePermuations(len(basePhaseSettings), basePhaseSettings, &result)

	var max float64 = 0
	var wg sync.WaitGroup

	chA := make(chan int)
	chB := make(chan int)
	chC := make(chan int)
	chD := make(chan int)
	chE := make(chan int)

	for _, setting := range result {
		wg.Add(5)

		go func() {
			defer wg.Done()
			intcodecomputer.InitilizeComputer(input).RunChannels(chE, chA)
			wg.Add(1)
			go func() {
				defer wg.Done()
				lastValue := <-chE
				max = math.Max(float64(lastValue), float64(max))
			}()
		}()
		go func() {
			defer wg.Done()
			intcodecomputer.InitilizeComputer(input).RunChannels(chA, chB)
		}()
		go func() {
			defer wg.Done()
			intcodecomputer.InitilizeComputer(input).RunChannels(chB, chC)
		}()
		go func() {
			defer wg.Done()
			intcodecomputer.InitilizeComputer(input).RunChannels(chC, chD)
		}()
		go func() {
			defer wg.Done()
			intcodecomputer.InitilizeComputer(input).RunChannels(chD, chE)
		}()

		chE <- setting[0]
		chA <- setting[1]
		chB <- setting[2]
		chC <- setting[3]
		chD <- setting[4]
		chE <- 0

		wg.Wait()
	}
	fmt.Println(int(max))
}
