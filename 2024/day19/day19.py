from runner import PuzzleRunner


def part1(lines):
    towels = tuple([x.strip() for x in lines[0].split(",")])
    count = 0

    for itr, line in enumerate(lines[2:]):
        pattern = line.strip()
        matching_towels = get_matching_towels(pattern, towels)
        if count_matching(pattern, matching_towels) == len(pattern):
            count += 1
    print(count)


def get_matching_towels(pattern, towels):
    res = []
    for t in towels:
        if t in pattern:
            res.append(t)
    return tuple(res)


def count_matching(pattern, towels):
    if len(pattern) <= 0:
        return 0

    if pattern in towels:
        return len(pattern)

    i = len(pattern)
    while i > 0:
        if pattern[:i] in towels:
            match_current = i + count_matching(pattern[i:], towels)
            if match_current == len(pattern):
                return len(pattern)

            match_current_minus = count_matching(pattern[:i - 1], towels) + count_matching(pattern[i - 1:], towels)
            if match_current_minus == len(pattern):
                return len(pattern)
        i -= 1

    return 0


PuzzleRunner().run(part1)
