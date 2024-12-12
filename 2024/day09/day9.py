input_file = open('day9.txt', 'r')
lines = input_file.readlines()


def part1():
    memory = []
    block_index = 0
    for index, char in enumerate(lines[0]):
        if index % 2 == 0:
            memory.extend([str(block_index)] * int(char))
            block_index += 1
        else:
            memory.extend(['.'] * int(char))

    left, right = 0, len(memory) - 1
    while left < right:
        if memory[left] != '.':
            left += 1
            continue
        if memory[right] == '.':
            right -= 1
            continue
        memory[left] = memory[right]
        memory[right] = '.'

    score = sum(i * int(val) for i, val in enumerate(memory[:memory.index('.')]))
    print(score)


def part2():
    print('')


part1()
part2()
