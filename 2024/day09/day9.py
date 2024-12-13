input_file = open('day9.txt', 'r')
lines = input_file.readlines()


def part1():
    memory = read_input_memory()
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
    memory = read_input_memory()
    right = len(memory) - 1

    visited = []
    while right > 0:
        if memory[right] == '.':
            right -= 1
            continue
        start_index = right
        nbr = memory[right]
        while right > 1:
            if memory[right - 1] == nbr:
                right -= 1
            else:
                break
        length = (start_index - right) + 1
        if nbr in visited:
            right -= 1
            continue
        visited.append(nbr)

        # loop left to right, find matching empty space
        count = 0
        start_index = 0
        for i, val in enumerate(memory):
            if i > right:
                break
            if val == '.':
                if count == 0:
                    start_index = i
                count += 1
            else:
                if count > 0:
                    if count >= length:
                        memory[start_index:start_index + length] = [nbr] * length
                        memory[right:right + length] = ['.'] * length
                        break
                    else:
                        count = 0
                        start_index = 0
        right -= 1

    score = sum(i * int(val) for i, val in enumerate(memory) if val.isdigit())
    print(score)


def read_input_memory():
    memory = []
    block_index = 0
    for index, char in enumerate(lines[0]):
        if index % 2 == 0:
            memory.extend([str(block_index)] * int(char))
            block_index += 1
        else:
            memory.extend(['.'] * int(char))
    return memory


part1()
part2()
