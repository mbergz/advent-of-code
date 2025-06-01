package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"bufio"
	"context"
	"fmt"
	"os"
	"os/signal"
	"strings"
	"syscall"
)

func main() {
	input, err := os.ReadFile("./day25.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
}

func part1(input string) {
	defer util.Timer()()
	inputChannel := make(chan int, 100)

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	go readInput(ctx, inputChannel)

	comp := intcodecomputer.InitilizeComputer(input)
	inputFn := func() int {
		val, ok := <-inputChannel
		if !ok {
			// Channel closed, user typed quit or received sigterm
			os.Exit(0)
		}
		return val
	}

	outputFn := func(next int) {
		fmt.Print(string(rune(next)))
	}

	comp.RunComputer(inputFn, outputFn, false)
}

func readInput(ctx context.Context, inputChannel chan int) {
	defer close(inputChannel)

	scanner := bufio.NewScanner(os.Stdin)
	for {
		select {
		case <-ctx.Done():
			return
		default:
			if !scanner.Scan() {
				// EOF or error
				return
			}
			text := strings.TrimSpace(scanner.Text())
			if text == "quit" {
				return
			}
			converted := convertToAscii(text)
			for _, val := range converted {
				inputChannel <- val
			}
			inputChannel <- '\n'

		}
	}
}

func convertToAscii(input string) []int {
	res := make([]int, 0)
	for _, ch := range input {
		res = append(res, int(ch))
	}
	return res
}
