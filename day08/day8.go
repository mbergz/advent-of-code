package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
)

const (
	rows    = 6
	columns = 25
)

type Layer [rows][columns]int

func main() {
	input, err := os.ReadFile("./day8.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	var layers []Layer = createLayers(input)

	lowestFoundIndex := 0
	lowest := math.MaxInt
	for i, l := range layers {
		zeroCount := 0
		for _, row := range l {
			for _, c := range row {
				if c == 0 {
					zeroCount++
				}
			}
		}
		if zeroCount < lowest {
			lowest = zeroCount
			lowestFoundIndex = i
		}
	}
	nbrOf1 := 0
	nbrOf2 := 0
	for _, row := range layers[lowestFoundIndex] {
		for _, c := range row {
			if c == 1 {
				nbrOf1++
			}
			if c == 2 {
				nbrOf2++
			}
		}
	}
	fmt.Println(nbrOf1 * nbrOf2)
}

func part2(input string) {
	defer util.Timer()()

	var layers []Layer = createLayers(input)

	var image Layer

	// 0 = black, 1 = white, 2 = transparent
	// Eyes >>>>>    Layer 1 --> Layer 2 --->... ---> Layer N

	for r := 0; r < rows; r++ {
		for c := 0; c < columns; c++ {
			visiblePixel := layers[0][r][c]
			if visiblePixel == 2 {
				for l := 1; l < len(layers); l++ {
					if layers[l][r][c] != 2 {
						visiblePixel = layers[l][r][c]
						break
					}
				}
			}
			image[r][c] = visiblePixel
		}
	}

	for _, row := range image {
		for _, val := range row {
			if val == 1 {
				fmt.Print("X ")
			} else {
				fmt.Print("  ")
			}
		}
		fmt.Println()
	}
}

func createLayers(input string) []Layer {
	var layers []Layer
	layerSize := rows * columns

	var currentLayer Layer
	currentLayer[0][0] = util.ToInt(string(input[0]))

	for i := 1; i < len(input); i++ {
		index := i % layerSize
		if index == 0 { // New layer
			layers = append(layers, currentLayer)
			currentLayer = Layer{}
		}
		rowIndex := index / columns
		columnIndex := index % columns
		currentLayer[rowIndex][columnIndex] = util.ToInt(string(input[i]))
	}
	layers = append(layers, currentLayer) // Add last built layer
	return layers
}
