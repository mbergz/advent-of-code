package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"context"
	"fmt"
	"os"
	"sync"
)

func main() {
	input, err := os.ReadFile("./day23.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
}

func part1(input string) {
	defer util.Timer()()

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	var inputQueues [50]chan int
	var outputChannels [50]chan int

	for i := 0; i < 50; i++ {
		comp := intcodecomputer.InitilizeComputer(input)

		inputQueue := make(chan int, 100)
		inputQueues[i] = inputQueue
		inputQueue <- i

		outputChannel := make(chan int)
		outputChannels[i] = outputChannel

		go comp.RunComputer(func() int {
			return nonBlockingInputFunc(inputQueue)
		}, func(i int) { outputChannel <- i }, false)

	}

	var wgDone sync.WaitGroup
	wgDone.Add(1)

	for i := 0; i < 50; i++ {
		go handleOutputChannel(ctx, outputChannels[i], inputQueues, &wgDone)
	}

	wgDone.Wait()
}

func nonBlockingInputFunc(inputQueue chan int) int {
	select {
	case val := <-inputQueue:
		return val
	default:
		return -1
	}
}

func handleOutputChannel(ctx context.Context, outputChannel chan int, inputQueues [50]chan int, wg *sync.WaitGroup) {
	buffer := make([]int, 0, 3)
	for {
		select {
		case <-ctx.Done():
			return

		case received, ok := <-outputChannel:
			if !ok {
				return
			}
			buffer = append(buffer, received)

			if len(buffer) == 3 {
				address := buffer[0]
				x := buffer[1]
				y := buffer[2]

				if address == 255 {
					fmt.Printf("Received value to address 255, Y=%d\n", y)
					wg.Done()
					return
				}

				targetQueue := inputQueues[address]
				targetQueue <- x
				targetQueue <- y

				// reset buffer
				buffer = buffer[:0]
			}
		}
	}

}
