from runner import PuzzleRunner


def part1(lines):
    score = 0
    for line in lines:
        parts = list(map(int, line.split()))
        if is_safe(parts):
            score += 1
    print(score)


def part2(lines):
    score = 0
    for line in lines:
        parts = list(map(int, line.split()))

        if is_safe(parts):
            score += 1
        else:
            for i in range(len(parts)):
                if is_safe(parts[:i] + parts[i + 1:]):
                    score += 1
                    break

    print(score)


def is_safe(levels):
    asc = levels[1] > levels[0]
    for i in range(1, len(levels)):
        if levels[i] == levels[i - 1]:
            return False
        if asc and levels[i] < levels[i - 1] or not asc and levels[i] > levels[i - 1]:
            return False
        if asc and levels[i] - levels[i - 1] > 3 or not asc and levels[i - 1] - levels[i] > 3:
            return False
    return True


PuzzleRunner().run(part1, part2)
