package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"strings"
)

func main() {
	input, err := os.ReadFile("./day6.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()
	orbitMap := make(map[string]string)
	for _, line := range strings.Split(input, "\n") {
		first := strings.TrimSpace(strings.Split(line, ")")[0])
		second := strings.TrimSpace(strings.Split(line, ")")[1])
		orbitMap[second] = first
	}

	total := 0
	for _, startValue := range orbitMap {
		value := startValue
		total++
		for value != "COM" {
			total++
			value = orbitMap[value]
		}
	}
	fmt.Println(total)
}

func part2(input string) {
	defer util.Timer()()
	orbitMap := make(map[string]string)
	for _, line := range strings.Split(input, "\n") {
		first := strings.TrimSpace(strings.Split(line, ")")[0])
		second := strings.TrimSpace(strings.Split(line, ")")[1])
		orbitMap[second] = first
	}

	youPath := getPathMapping(orbitMap, "YOU")
	sanPath := getPathMapping(orbitMap, "SAN")

	lowest := math.MaxInt
	for key, valYou := range youPath {
		valSan, ok := sanPath[key]
		if ok {
			lowest = min(lowest, valSan+valYou)
		}
	}
	fmt.Println(lowest)
}

func getPathMapping(orbitMap map[string]string, start string) map[string]int {
	path := make(map[string]int)
	total1 := 0
	value := orbitMap[start]
	for value != "COM" {
		total1++
		value = orbitMap[value]
		path[value] = total1
	}
	return path
}
