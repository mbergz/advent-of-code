package main

import (
	"advent-of-code_2019/util"
	"fmt"
	"os"
	"strings"
)

type ChemicalAmount struct {
	amount   int
	chemical string
}

type ReqAmount struct {
	required int
	spare    int
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

func part2(input string) {
	defer util.Timer()()

}

func calculateOreReqsRecursive(
	currentChemical ChemicalAmount,
	reactions map[ChemicalAmount][]ChemicalAmount,
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

func getInputChemicals(currentChemical ChemicalAmount, reactions map[ChemicalAmount][]ChemicalAmount, producedMap map[string]ReqAmount) []ChemicalAmount {
	multiplier := 1
	for key, value := range reactions {
		if key.chemical == currentChemical.chemical {
			resultSlice := make([]ChemicalAmount, 0)

			// key.amount = reaction output amount 10 ORE => 10 A
			amount := key.amount
			need := currentChemical.amount

			// Do I already have any spare of this chemical?
			if produced, ok := producedMap[key.chemical]; ok {
				if produced.spare > 0 {
					if produced.spare >= need {
						producedMap[key.chemical] = ReqAmount{produced.required + currentChemical.amount, produced.spare - need} // Reduce amount taken
						// I have enough spare to use, no need to create this chemical
						continue
					} else { // use as much there is
						need = need - produced.spare
						producedMap[key.chemical] = ReqAmount{produced.required, 0} // Reset spare
					}
				}
			}

			for amount < need {
				multiplier++
				amount = key.amount * multiplier
			}
			newSpareAmount := (key.amount * multiplier) - need
			if val, ok := producedMap[key.chemical]; ok {
				producedMap[key.chemical] = ReqAmount{val.required + currentChemical.amount, newSpareAmount}
			} else {
				producedMap[key.chemical] = ReqAmount{currentChemical.amount, newSpareAmount}
			}

			for _, val := range value {
				newAmount := val.amount * multiplier
				resultSlice = append(resultSlice, ChemicalAmount{amount: newAmount, chemical: val.chemical})
			}

			return resultSlice
		}
	}
	return []ChemicalAmount{}
}

func createReactions(input string) map[ChemicalAmount][]ChemicalAmount {
	reactions := make(map[ChemicalAmount][]ChemicalAmount)

	for _, line := range strings.Split(input, "\n") {
		parts := strings.Split(line, "=>")
		output := createChemicalAmount(strings.TrimSpace(parts[1]))
		input := make([]ChemicalAmount, 0)
		for _, part := range strings.Split(parts[0], ",") {
			input = append(input, createChemicalAmount(strings.TrimSpace(part)))
		}
		reactions[output] = input
	}
	return reactions
}

func createChemicalAmount(input string) ChemicalAmount {
	parts := strings.Split(input, " ")
	amount := util.ToInt(strings.TrimSpace(parts[0]))
	chemical := strings.TrimSpace(parts[1])
	return ChemicalAmount{amount, chemical}
}
