from runner import PuzzleRunner


def part1(lines):
    locks = []
    keys = []

    for row, line in enumerate(lines):
        if line == "\n":
            if lines[row - 7][0] == "#":
                locks.append(create_lock(lines[row - 6: row]))
            else:
                keys.append(create_key(lines[row - 7: row]))
    if lines[len(lines) - 7][0] == "#":
        locks.append(create_lock(lines[len(lines) - 6: len(lines) + 1]))
    else:
        keys.append(create_key(lines[len(lines) - 7: len(lines) + 1]))

    valid_combos = 0
    for key in keys:
        for lock in locks:
            for i in range(5):
                if key[i] + lock[i] > 5:
                    break
            else:
                valid_combos += 1

    print(valid_combos)


def create_lock(lock_line):
    res = []
    for c in range(5):
        curr = 0
        for r in range(6):
            if lock_line[r][c] != "#":
                res.append(curr)
                break
            curr += 1
    return res


def create_key(key_line):
    res = []
    for c in range(5):
        curr = 0
        for r in range(5, -1, -1):
            if key_line[r][c] != "#":
                res.append(curr)
                break
            curr += 1
    return res


PuzzleRunner().run(part1)
