package main

import (
	"advent-of-code_2019/intcodecomputer"
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
)

type Direction int

const (
	UP Direction = iota
	RIGHT
	DOWN
	LEFT
)

var turnLeft = map[Direction]Direction{
	UP:    LEFT,
	RIGHT: UP,
	DOWN:  RIGHT,
	LEFT:  DOWN,
}

var turnRight = map[Direction]Direction{
	UP:    RIGHT,
	RIGHT: DOWN,
	DOWN:  LEFT,
	LEFT:  UP,
}

type Coord struct {
	x int
	y int
}

func main() {
	input, err := os.ReadFile("./day11.txt")
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
	quit := make(chan bool)

	totalCount := 0

	go func() {
		comp := intcodecomputer.InitilizeComputer(input)
		comp.RunChannels(inputChannel, outputChannel)
		close(outputChannel)
		fmt.Println(totalCount)
		quit <- true
	}()

	var panels map[Coord]int = make(map[Coord]int, 0)
	current := Coord{0, 0}
	direction := UP

	for {
		select {
		case <-quit:
			return
		case inputChannel <- panels[current]:
			// Color
			output1 := <-outputChannel

			_, exists := panels[current]
			if !exists {
				totalCount++ // New squre
			}
			if output1 == 0 { // Paint black
				panels[current] = 0
			} else if output1 == 1 { // Paint white
				panels[current] = 1
			}
			// Direction
			output2 := <-outputChannel

			if output2 == 0 {
				current, direction = turn90Left(current, direction)
			} else if output2 == 1 {
				current, direction = turn90Right(current, direction)
			}
		}
	}
}

func part2(input string) {
	defer util.Timer()()

	inputChannel := make(chan int)
	outputChannel := make(chan int)
	quit := make(chan bool)

	var panels map[Coord]int = make(map[Coord]int, 0)
	current := Coord{0, 0}
	panels[current] = 1
	direction := UP

	go func() {
		comp := intcodecomputer.InitilizeComputer(input)
		comp.RunChannels(inputChannel, outputChannel)
		close(outputChannel)
		quit <- true
	}()

mainLoop:
	for {
		select {
		case <-quit:
			break mainLoop
		case inputChannel <- panels[current]:
			// Color
			output1 := <-outputChannel

			if output1 == 0 { // Paint black
				panels[current] = 0
			} else if output1 == 1 { // Paint white
				panels[current] = 1
			}
			// Direction
			output2 := <-outputChannel

			if output2 == 0 {
				current, direction = turn90Left(current, direction)
			} else if output2 == 1 {
				current, direction = turn90Right(current, direction)
			}
		}
	}

	minX, minY := math.MaxInt, math.MaxInt
	maxX, maxY := math.MinInt, math.MinInt

	for key, _ := range panels {
		minX = min(minX, key.x)
		minY = min(minY, key.y)
		maxX = max(maxX, key.x)
		maxY = max(maxY, key.y)
	}

	for i := minY - 1; i <= maxY+1; i++ {
		for j := minX - 1; j <= maxX+1; j++ {
			if value, ok := panels[Coord{j, i}]; ok && value == 1 {
				fmt.Print("# ")
			} else {
				fmt.Print(". ")
			}
		}
		fmt.Print("\n")
	}
}

func turn90Left(current Coord, currentDirection Direction) (Coord, Direction) {
	newDirection := turnLeft[currentDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord, newDirection
}

func turn90Right(current Coord, currentDirection Direction) (Coord, Direction) {
	newDirection := turnRight[currentDirection]
	newCoord := calcNewCoord(current, newDirection)
	return newCoord, newDirection
}

func calcNewCoord(current Coord, newDirection Direction) Coord {
	switch newDirection {
	case UP:
		return Coord{current.x, current.y - 1}
	case RIGHT:
		return Coord{current.x + 1, current.y}
	case DOWN:
		return Coord{current.x, current.y + 1}
	case LEFT:
		return Coord{current.x - 1, current.y}
	}
	return Coord{}
}
