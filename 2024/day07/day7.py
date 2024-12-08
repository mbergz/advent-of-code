input_file = open('day7.txt', 'r')
lines = input_file.readlines()


def part1():
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


def part2():
    print("count")


part1()
part2()
