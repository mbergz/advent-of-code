package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"os"
	"slices"
	"strconv"
	"strings"
)

const DeckSize = 10007

func main() {
	input, err := os.ReadFile("./day22.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
}

func part1(input string) {
	defer util.Timer()()

	deck := make([]int, DeckSize)
	for i := 0; i < DeckSize; i++ {
		deck[i] = i
	}

	for _, line := range strings.Split(input, "\n") {
		if strings.Contains(line, "deal with increment") {
			deck = dealWithIncrement(deck, extractN(line))
		} else if strings.Contains(line, "deal into new stack") {
			dealIntoNewStack(deck)
		} else if strings.Contains(line, "cut") {
			deck = cutCards(deck, extractN(line))
		}
	}

	for i, v := range deck {
		if v == 2019 {
			fmt.Println(i)
			return
		}
	}
}

func dealIntoNewStack(deck []int) {
	slices.Reverse(deck)
}

func extractN(line string) int {
	parts := strings.Fields(line)
	last := parts[len(parts)-1]
	n, err := strconv.Atoi(last)
	if err != nil {
		fmt.Println("Error:", err)
		return -1
	}
	return n
}

func cutCards(deck []int, n int) []int {
	if n > 0 {
		cut := deck[:n]
		newDeck := append(deck[n:], cut...)
		return newDeck
	} else {
		n = len(deck) - int(math.Abs((float64(n))))
		cut := deck[n:]
		newDeck := append(cut, deck[:n]...)
		return newDeck
	}
}

func dealWithIncrement(deck []int, n int) []int {
	newDeck := make([]int, len(deck))

	for i := 0; i < DeckSize; i++ {
		newDeck[(i*n)%DeckSize] = deck[i]
	}

	return newDeck
}
