import functools

from runner import PuzzleRunner


def part1(lines):
    towels = tuple([x.strip() for x in lines[0].split(",")])
    count = 0

    for itr, line in enumerate(lines[2:]):
        pattern = line.strip()
        matching_towels = get_matching_towels(pattern, towels)
        if is_possible(pattern, matching_towels):
            count += 1
    print(count)


def part2(lines):
    towels = tuple([x.strip() for x in lines[0].split(",")])
    count = 0

    for itr, line in enumerate(lines[2:]):
        pattern = line.strip()
        matching_towels = get_matching_towels(pattern, towels)
        count += count_valid_combinations(pattern, matching_towels)
    print(count)


def get_matching_towels(pattern, towels):
    res = []
    for t in towels:
        if t in pattern:
            res.append(t)
    return tuple(res)


def is_possible(pattern, towels):
    if len(pattern) <= 0:
        return True

    for i in range(1, len(pattern) + 1):
        if pattern[:i] in towels:
            if is_possible(pattern[i:], towels):
                return True

    return False


@functools.cache
def count_valid_combinations(pattern, towels):
    if len(pattern) == 0:
        return 1

    count = 0
    for i in range(1, len(pattern) + 1):
        if pattern[:i] in towels:
            count += count_valid_combinations(pattern[i:], towels)

    return count


PuzzleRunner().run(part1, part2)
