from runner import PuzzleRunner


def part1(lines):
    first, second = create_parts(lines)
    diff = 0
    for i in range(len(first)):
        diff += abs(int(second[i]) - int(first[i]))
    print(diff)


def part2(lines):
    first, second = create_parts(lines)
    second_map = {}
    for x in second:
        second_map[x] = second_map.get(x, 0) + 1
    score = sum(int(x) * second_map.get(x, 0) for x in first)
    print(score)


def create_parts(lines):
    first, second = [], []
    for line in lines:
        split = line.split()
        first.append(split[0])
        second.append(split[1])
    return sorted(first), sorted(second)


PuzzleRunner().run(part1, part2)
