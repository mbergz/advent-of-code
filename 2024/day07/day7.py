import numpy as np

from runner import PuzzleRunner


def part1(lines):
    score = 0
    for line in lines:
        test_value = int(line.split(':')[0])
        input_values = line.split(':')[1].strip().split(' ')

        combinations = 2 ** (len(input_values) - 1)
        for i in range(0, combinations):
            combination = bin(i)[2:].zfill((len(input_values) - 1))

            out = int(input_values[0])
            for index, value in enumerate(input_values[1:], start=1):
                out = out + int(value) if combination[index - 1] == '0' else out * int(value)

            if out == test_value:
                score += test_value
                break
    print(score)


def part2(lines):
    score = 0
    for line in lines:
        test_value = int(line.split(':')[0])
        input_values = line.split(':')[1].strip().split(' ')

        combinations = 3 ** (len(input_values) - 1)
        for i in range(0, combinations):
            combination = np.base_repr(i, base=3).zfill((len(input_values) - 1))

            out = int(input_values[0])
            for index, value in enumerate(input_values[1:], start=1):
                if combination[index - 1] == '0':
                    out = out + int(value)
                elif combination[index - 1] == '1':
                    out = out * int(value)
                else:  # ||
                    out = int(str(out) + value)

            if out == test_value:
                score += test_value
                break
    print(score)


PuzzleRunner().run(part1, part2)
