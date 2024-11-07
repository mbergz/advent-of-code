package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"bytes"
	"fmt"
	"io"
	"math"
	"os"
	"strconv"
	"sync"
	"time"
)

type TileType int

const (
	EMPTY TileType = iota
	WALL
	BLOCK
	H_PADDLE
	BALL
)

type Coord struct {
	x int
	y int
}

var (
	maxX         int
	maxY         int
	currentScore int
)

func main() {
	input, err := os.ReadFile("./day13.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	inputChannel := make(chan int)
	outputChannel := make(chan int)

	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		defer wg.Done()
		defer close(inputChannel)

		comp := intcodecomputer.InitilizeComputer(input)
		comp.RunChannels(inputChannel, outputChannel)
		close(outputChannel)
	}()

	grid := make(map[Coord]TileType, 0)

	go func() {
		defer wg.Done()

		count := 0
		var x int
		var y int
		var tileId TileType

		for output := range outputChannel {
			count++
			if count == 1 {
				x = output
			}
			if count == 2 {
				y = output
			}
			if count == 3 {
				tileId = TileType(output)
				if EMPTY != tileId {
					grid[Coord{x, y}] = tileId
				}
				count = 0
			}
		}
	}()

	wg.Wait()

	blockCount := 0
	for _, value := range grid {
		if value == BLOCK {
			blockCount++
		}
	}
	fmt.Println(blockCount)
}

func part2(input string) {
	defer util.Timer()()

	inputChannel := make(chan int)
	outputChannel := make(chan int)

	var wg sync.WaitGroup
	wg.Add(4)

	go func() {
		defer wg.Done()

		modifiedInput := []rune(input)
		modifiedInput[0] = '2'

		comp := intcodecomputer.InitilizeComputer(string(modifiedInput))
		comp.RunChannels(inputChannel, outputChannel)
		close(outputChannel)
	}()

	gameActive := false
	quit := false
	grid := make(map[Coord]TileType, 0)

	go func() {
		defer wg.Done()

		count := 0
		var x int
		var y int
		var tileId TileType

		for output := range outputChannel {
			count++
			if count == 1 {
				x = output
			}
			if count == 2 {
				y = output
			}
			if count == 3 {
				if x == -1 && y == 0 {
					if !gameActive { // Init
						maxX = math.MinInt
						maxY = math.MinInt

						for key, _ := range grid {
							maxX = max(maxX, key.x)
							maxY = max(maxY, key.y)
						}

						gameActive = true
					}

					currentScore = output
				} else {
					tileId = TileType(output)
					grid[Coord{x, y}] = tileId
				}
				count = 0
			}
		}
	}()

	go func() {
		defer wg.Done()

		var b []byte = make([]byte, 3)

		for {
			os.Stdin.Read(b)

			if string(b[0]) == "q" {
				quit = true
				break
			}
		}
	}()

	go func() {
		defer wg.Done()

		for {
			if quit {
				break
			}

			if gameActive {
				printGameState(grid)
			}

			xBall := 0
			xPaddle := 0

			for key, value := range grid {
				if value == BALL {
					xBall = key.x
				}
				if value == H_PADDLE {
					xPaddle = key.x
				}
			}

			if xBall == xPaddle {
				inputChannel <- 0
			} else if xBall > xPaddle {
				inputChannel <- 1
			} else {
				inputChannel <- -1
			}

			time.Sleep(10 * time.Millisecond)
		}
	}()

	wg.Wait()
}

func printGameState(grid map[Coord]TileType) {
	var buf bytes.Buffer

	buf.WriteString("Score: " + strconv.Itoa(currentScore) + "\n\n")

	for i := 0; i <= maxY+1; i++ {
		for j := 0; j <= maxX+1; j++ {
			val, exists := grid[Coord{j, i}]
			if exists {
				if val == EMPTY {
					buf.WriteString(" ")
				}
				if val == WALL {
					buf.WriteString("W")
				}
				if val == BLOCK {
					buf.WriteString("B")
				}
				if val == H_PADDLE {
					buf.WriteString("_")
				}
				if val == BALL {
					buf.WriteString("o")
				}
			} else {
				buf.WriteString(" ")
			}
		}
		buf.WriteByte('\n')
	}

	io.Copy(os.Stdout, bytes.NewBuffer(buf.Bytes()))
}
