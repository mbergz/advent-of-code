from collections import defaultdict

from runner import PuzzleRunner


def part1(lines):
    mapping = defaultdict(set)
    for line in lines:
        a, b = line.strip().split("-")
        mapping[a].add(b)
        mapping[b].add(a)

    result = set()
    for key, value in mapping.items():
        for v in value:
            for both_match in value & mapping[v]:
                if key[0] == "t" or v[0] == "t" or both_match[0] == "t":
                    result.add(tuple(sorted([key, v, both_match])))

    print(len(result))


def part2(lines):
    mapping = defaultdict(set)
    for line in lines:
        a, b = line.strip().split("-")
        mapping[a].add(b)
        mapping[b].add(a)

    max_connected = set()

    for key, value in mapping.items():
        matching = set(value)
        matching.add(key)

        for v in value:
            if v in matching:
                copy_set_b = set(mapping[v])
                copy_set_b.add(v)
                matching = matching & copy_set_b

        max_connected = matching if len(matching) > len(max_connected) else max_connected

    print(",".join(sorted(max_connected)))


PuzzleRunner().run(part1, part2)
