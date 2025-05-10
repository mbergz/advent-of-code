package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"bufio"
	"fmt"
	"os"
	"strings"
	"sync"
)

func main() {
	input, err := os.ReadFile("./day21.txt")
	if err != nil {
		panic(err)
	}
	run(string(input))
}

func run(input string) {
	defer util.Timer()()

	comp := intcodecomputer.InitilizeComputer(input)

	inputChannel := make(chan int)
	outputChannel := make(chan int)

	var wg sync.WaitGroup
	wg.Add(2)

	go sendUserInput(inputChannel, &wg)

	go func() {
		defer wg.Done()

		for output := range outputChannel {
			if output >= 0 && output <= 127 {
				fmt.Print(convertAsciiToString([]int{output}))
			} else {
				fmt.Print(output)
			}
		}
		fmt.Printf("\n\nSuccessful. Write q or exit to exit the program\n")
	}()

	comp.RunChannels(inputChannel, outputChannel)
	close(outputChannel)

	wg.Wait()
}

func sendUserInput(inputChannel chan int, wg *sync.WaitGroup) {
	defer wg.Done()
	defer close(inputChannel)

	reader := bufio.NewReader(os.Stdin)

	for {

		text, err := reader.ReadString('\n')
		text = strings.ReplaceAll(text, "\r\n", "\n")
		if err != nil {
			break
		}
		if strings.TrimSpace(text) == "q" || strings.TrimSpace(text) == "exit" {
			return
		}
		for _, ascii := range convertToAscii(text) {
			inputChannel <- ascii
		}

	}
}

func convertToAscii(input string) []int {
	res := make([]int, len(input))
	for i, ch := range input {
		res[i] = int(ch)
	}
	return res
}

func convertAsciiToString(input []int) string {
	res := make([]rune, len(input))
	for i, ch := range input {
		res[i] = rune(ch)
	}
	return string(res)
}
