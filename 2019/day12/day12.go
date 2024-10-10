package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"regexp"
	"strings"
)

type Coordinate struct {
	x int
	y int
	z int
}

type Moon struct {
	pos  Coordinate
	velo Coordinate
}

type Pair struct {
	first  *Moon
	second *Moon
}

func main() {
	input, err := os.ReadFile("./day12.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	nbrOfSteps := 1000

	lines := strings.Split(input, "\n")
	io := createMoon(lines[0])
	europa := createMoon(lines[1])
	ganymede := createMoon(lines[2])
	callisto := createMoon(lines[3])

	moons := make([]*Moon, 0)
	moons = append(moons, &io)
	moons = append(moons, &europa)
	moons = append(moons, &ganymede)
	moons = append(moons, &callisto)

	pairs := makePairs(moons)

	for i := 0; i < nbrOfSteps; i++ {
		calculateVelocity(pairs)
		applyVelocity(moons)
	}

	total := 0
	for _, moon := range moons {
		total += (util.AbsInt(moon.pos.x) + util.AbsInt(moon.pos.y) + util.AbsInt(moon.pos.z)) *
			(util.AbsInt(moon.velo.x) + util.AbsInt(moon.velo.y) + util.AbsInt(moon.velo.z))
	}

	fmt.Println(total)
}

func part2(input string) {
	defer util.Timer()()

}

func applyVelocity(moons []*Moon) {
	for _, moon := range moons {
		moon.pos.x = moon.pos.x + moon.velo.x
		moon.pos.y = moon.pos.y + moon.velo.y
		moon.pos.z = moon.pos.z + moon.velo.z
	}
}

func calculateVelocity(pairs []Pair) {
	for _, pair := range pairs {

		if pair.first.pos.x != pair.second.pos.x {
			if pair.first.pos.x < pair.second.pos.x {
				pair.first.velo.x++
				pair.second.velo.x--
			} else {
				pair.first.velo.x--
				pair.second.velo.x++
			}
		}

		if pair.first.pos.y != pair.second.pos.y {
			if pair.first.pos.y < pair.second.pos.y {
				pair.first.velo.y++
				pair.second.velo.y--
			} else {
				pair.first.velo.y--
				pair.second.velo.y++
			}
		}

		if pair.first.pos.z != pair.second.pos.z {
			if pair.first.pos.z < pair.second.pos.z {
				pair.first.velo.z++
				pair.second.velo.z--
			} else {
				pair.first.velo.z--
				pair.second.velo.z++
			}
		}

	}
}

func makePairs(moons []*Moon) []Pair {
	pairs := make([]Pair, 0)
	for i := 0; i < len(moons); i++ {
		for j := (i + 1); j < len(moons); j++ {
			pairs = append(pairs, Pair{moons[i], moons[j]})
		}
	}
	return pairs
}

func createMoon(input string) Moon {
	parts := strings.Split(input, ",")
	var re = regexp.MustCompile(`[<>\s\r,]`)
	x := util.ToInt(strings.Split(re.ReplaceAllString(parts[0], ""), "=")[1])
	y := util.ToInt(strings.Split(re.ReplaceAllString(parts[1], ""), "=")[1])
	z := util.ToInt(strings.Split(re.ReplaceAllString(parts[2], ""), "=")[1])
	return Moon{pos: Coordinate{x: x, y: y, z: z}}
}
