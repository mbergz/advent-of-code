package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"sort"
	"strings"
)

type ReactionMapValue struct {
	amount int
	input  []ChemicalAmount
}

type ChemicalAmount struct {
	amount   int
	chemical string
}

type ReqAmount struct {
	producedOre int
	required    int
	spare       int
}

func main() {
	input, err := os.ReadFile("./day14.txt")
	if err != nil {
		panic(err)
	}
	part1(string(input))
	part2(string(input))
}

func part1(input string) {
	defer util.Timer()()

	reactions := createReactions(input)
	producedMap := make(map[string]ReqAmount)
	totalOre := calculateOreReqsRecursive(ChemicalAmount{1, "FUEL"}, reactions, producedMap)
	fmt.Println("Total ore required:", totalOre)
}

// Credit to reddit for hint using binary search
func part2(input string) {
	defer util.Timer()()

	reactions := createReactions(input)

	producedMap := make(map[string]ReqAmount)
	oreReq1Fuel := calculateOreReqsRecursive(ChemicalAmount{1, "FUEL"}, reactions, producedMap)
	fmt.Println("oreReq1Fuel is ", oreReq1Fuel)

	res := sort.Search(5586022, func(i int) bool {
		producedMap := make(map[string]ReqAmount)
		totalOre := calculateOreReqsRecursive(ChemicalAmount{i, "FUEL"}, reactions, producedMap)
		return totalOre >= 1_000_000_000_000
	})

	fmt.Println(res - 1)

}

func calculateOreReqsRecursive(
	currentChemical ChemicalAmount,
	reactions map[string]ReactionMapValue,
	producedMap map[string]ReqAmount) int {

	oreCount := 0
	fuelReactionInput := getInputChemicals(currentChemical, reactions, producedMap)

	for _, input := range fuelReactionInput {
		if input.chemical == "ORE" {
			oreCount += input.amount
		} else {
			oreCount += calculateOreReqsRecursive(input, reactions, producedMap)
		}
	}
	return oreCount
}

func getInputChemicals(currentChemical ChemicalAmount, reactions map[string]ReactionMapValue, producedMap map[string]ReqAmount) []ChemicalAmount {
	chemical := currentChemical.chemical
	if reactionValue, ok := reactions[chemical]; ok {
		resultSlice := make([]ChemicalAmount, 0)

		// key.amount = reaction output amount 10 ORE => 10 A
		amount := reactionValue.amount
		need := currentChemical.amount

		// Do I already have any spare of this chemical?
		if produced, ok := producedMap[chemical]; ok {
			if produced.spare > 0 {
				if produced.spare >= need {
					producedMap[chemical] = ReqAmount{0, produced.required + currentChemical.amount, produced.spare - need} // Reduce amount taken
					// I have enough spare to use, no need to create this chemical
					return []ChemicalAmount{}
				} else { // use as much there is
					need = need - produced.spare
					producedMap[chemical] = ReqAmount{0, produced.required, 0} // Reset spare
				}
			}
		}

		multiplier := need / amount
		if reactionValue.amount*multiplier < need {
			multiplier++
		}

		newSpareAmount := (reactionValue.amount * multiplier) - need
		if val, ok := producedMap[chemical]; ok {
			producedMap[chemical] = ReqAmount{0, val.required + currentChemical.amount, newSpareAmount}
		} else {
			producedMap[chemical] = ReqAmount{0, currentChemical.amount, newSpareAmount}
		}

		for _, val := range reactionValue.input {
			newAmount := val.amount * multiplier
			resultSlice = append(resultSlice, ChemicalAmount{amount: newAmount, chemical: val.chemical})
		}

		return resultSlice
	}
	return []ChemicalAmount{}
}

func createReactions(input string) map[string]ReactionMapValue {
	reactions := make(map[string]ReactionMapValue)

	for _, line := range strings.Split(input, "\n") {
		parts := strings.Split(line, "=>")
		output := createChemicalAmount(strings.TrimSpace(parts[1]))
		input := make([]ChemicalAmount, 0)
		for _, part := range strings.Split(parts[0], ",") {
			input = append(input, createChemicalAmount(strings.TrimSpace(part)))
		}
		reactions[output.chemical] = ReactionMapValue{output.amount, input}
	}
	return reactions
}

func createChemicalAmount(input string) ChemicalAmount {
	parts := strings.Split(input, " ")
	amount := util.ToInt(strings.TrimSpace(parts[0]))
	chemical := strings.TrimSpace(parts[1])
	return ChemicalAmount{amount, chemical}
}
