file1 = open('day1.txt', 'r')
lines = file1.readlines()


def part1():
    first, second = create_parts()
    diff = 0
    for i in range(len(first)):
        diff += abs(int(second[i]) - int(first[i]))
    print(diff)


def part2():
    first, second = create_parts()
    second_map = {}
    for x in second:
        second_map[x] = second_map.get(x, 0) + 1
    score = sum(int(x) * second_map.get(x, 0) for x in first)
    print(score)


def create_parts():
    first, second = [], []
    for line in lines:
        split = line.split()
        first.append(split[0])
        second.append(split[1])
    return sorted(first), sorted(second)


part1()
part2()
