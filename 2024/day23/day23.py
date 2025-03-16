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


PuzzleRunner().run(part1)
