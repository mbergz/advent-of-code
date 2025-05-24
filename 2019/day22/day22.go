package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"math"
	"math/big"
	"os"
	"slices"
	"strconv"
	"strings"
)

const DeckSize1 = 10007
const DeckSize2 = 119315717514047

func main() {
	input, err := os.ReadFile("./day22.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	deck := make([]int, DeckSize1)
	for i := 0; i < DeckSize1; i++ {
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

// Inspo from https://codeforces.com/blog/entry/72593
func part2(input string) {
	defer util.Timer()()

	a := big.NewInt(1)
	b := big.NewInt(0)

	one := big.NewInt(1)
	negOne := big.NewInt(-1)

	m := big.NewInt(DeckSize2)
	//m := big.NewInt(DeckSize1) // for veriyfing part1

	// Linear congruential function f(x)=ax+b mod m
	// Compose f(x)=a1x+b1
	// g(x)=a2x+b2
	// g(f(x))=a2(a1x+b1) + b2 = (a2a1)x + (a2b1+b2)
	// a=a2*a1 % m
	// b=a2*b1 + b2 % m
	for _, line := range strings.Split(input, "\n") {
		if strings.Contains(line, "deal with increment") {
			// (x*n) mod m
			n := big.NewInt(int64(extractN(line)))
			a.Mul(a, n).Mod(a, m)
			b.Mul(b, n).Mod(b, m)
		} else if strings.Contains(line, "deal into new stack") {
			// (-1 -x) mod m
			a.Mul(a, negOne).Mod(a, m)
			b.Mul(b, negOne)
			b.Sub(b, one).Mod(b, m)
		} else if strings.Contains(line, "cut") {
			// (x-n) mod m
			n := big.NewInt(int64(extractN(line)))
			b.Sub(b, n).Mod(b, m)
		}
	}

	// Could verify part1 here with x=2019 and change m above to DeckSize1 and do F(x) = ax+b mod m

	// Now for part2, we need to repeat this k times and also do the inverse, namely for pos x find value
	// Repeat formula k times
	// f^k(x) = a^k*x + b* ((1-a^k)* (1-a)^-1 ) mod m
	// Now we want the inverse
	// x = A*F-k(x)+B mod m, F^-k(x) = (x - B / A) mod m

	k := big.NewInt(101741582076661)
	x := big.NewInt(2020)

	ak := new(big.Int).Exp(a, k, m) // A = a^k mod m

	// (1-a^k)
	oneMinusAk := new(big.Int).Mod(new(big.Int).Sub(one, ak), m)
	// (1-a)
	oneMinusA := new(big.Int).Mod(new(big.Int).Sub(one, a), m)
	// (1-a)^-1
	oneMinusAInv := new(big.Int).ModInverse(oneMinusA, m)
	// b*(1-a^k)
	numerator := new(big.Int).Mod(new(big.Int).Mul(b, oneMinusAk), m)
	// B = b*(1-a^k)*1-a)^-1
	B := new(big.Int).Mod(new(big.Int).Mul(numerator, oneMinusAInv), m)
	// x-B
	resNumerator := new(big.Int).Mod(new(big.Int).Sub(x, B), m)
	// A^-1 where A=a^k
	akInv := new(big.Int).ModInverse(ak, m)
	// (x-B)/A => (x-B)*A^-1
	res := resNumerator.Mod(new(big.Int).Mul(resNumerator, akInv), m)
	fmt.Println(res.String())
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

	for i := 0; i < DeckSize1; i++ {
		newDeck[(i*n)%DeckSize1] = deck[i]
	}

	return newDeck
}
