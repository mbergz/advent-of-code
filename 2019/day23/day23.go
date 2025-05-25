package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"context"
	"fmt"
	"os"
	"sync"
	"time"
)

func main() {
	input, err := os.ReadFile("./day23.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
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

func part2(input string) {
	defer util.Timer()()

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	nat := nat{}

	var computerIos [50]*computerIo

	for i := 0; i < 50; i++ {
		inputChannel := make(chan int, 100)
		inputChannel <- i // Assign address

		computerIo := &computerIo{address: i, inputChannel: inputChannel, idle: false, outputBuffer: make([]int, 0), outputidx: 0}
		computerIos[i] = computerIo
	}

	for i := 0; i < 50; i++ {
		go intcodecomputer.InitilizeComputer(input).RunComputer(
			func() int {
				return inputFuncPart2(computerIos[i])
			}, func(outVal int) {
				computerIos[i].handleOutputValue(outVal, computerIos, &nat)
			},
			false)

	}

	var wgDone sync.WaitGroup
	wgDone.Add(1)

	go performNatCheck(ctx, &nat, computerIos, &wgDone)

	wgDone.Wait()
}

type computerIo struct {
	address      int
	inputChannel chan int
	idle         bool
	outputBuffer []int
	outputidx    int
	mu           sync.Mutex
	idleMu       sync.Mutex
}

func (ci *computerIo) isIdle() bool {
	ci.idleMu.Lock()
	defer ci.idleMu.Unlock()
	return ci.idle
}

func (ci *computerIo) setIdle(value bool) {
	ci.idleMu.Lock()
	defer ci.idleMu.Unlock()
	ci.idle = value
}

func (ci *computerIo) handleOutputValue(value int, computerIos [50]*computerIo, nat *nat) {
	ci.mu.Lock()
	defer ci.mu.Unlock()

	ci.outputBuffer = append(ci.outputBuffer, value)
	if len(ci.outputBuffer) == 3 {
		address := ci.outputBuffer[0]
		x := ci.outputBuffer[1]
		y := ci.outputBuffer[2]

		if address == 255 {
			nat.set(x, y)
		} else {
			target := computerIos[address]
			target.setIdle(false)

			target.inputChannel <- x
			target.inputChannel <- y
		}

		ci.outputBuffer = ci.outputBuffer[:0]
		ci.outputidx = 0
	}
	ci.outputidx++
}

type nat struct {
	x  int
	y  int
	mu sync.Mutex
}

func (n *nat) set(x, y int) {
	n.mu.Lock()
	defer n.mu.Unlock()
	n.x, n.y = x, y
}

func (n *nat) get() (int, int) {
	n.mu.Lock()
	defer n.mu.Unlock()
	return n.x, n.y
}

func inputFuncPart2(compIo *computerIo) int {
	compIo.mu.Lock()
	defer compIo.mu.Unlock()

	select {
	case val := <-compIo.inputChannel:
		compIo.setIdle(false)
		return val
	default:
		compIo.setIdle(true)
		return -1
	}
}

func performNatCheck(ctx context.Context, nat *nat, compIos [50]*computerIo, wg *sync.WaitGroup) {
	seenYElements := make(map[int]bool)

	for {
		select {
		case <-ctx.Done():
			return
		default:
			time.Sleep(10 * time.Millisecond)

			allEmpty := true
			for i := 0; i < 50; i++ {
				if !compIos[i].isIdle() {
					allEmpty = false
					break
				}
			}
			if allEmpty {
				x, y := nat.get()

				compIos[0].setIdle(false)
				compIos[0].inputChannel <- x
				compIos[0].inputChannel <- y

				if _, ok := seenYElements[y]; ok {
					fmt.Println("Second time Y from NAT to address 0 comp is", y)
					wg.Done()
					return
				} else {
					seenYElements[y] = true
				}

			}
		}
	}
}
