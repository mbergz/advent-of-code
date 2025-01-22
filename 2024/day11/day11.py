import functools

from runner import PuzzleRunner


def part1(lines):
    stones = [int(nbr.strip()) for nbr in lines[0].split(" ")]
    score = sum(stone_blink(stone, 0, 25) for stone in stones)
    print(score)


def part2(lines):
    stones = [int(nbr.strip()) for nbr in lines[0].split(" ")]
    score = sum(stone_blink(stone, 0, 75) for stone in stones)
    print(score)


@functools.cache
def stone_blink(stone, itr, itr_limit):
    if itr == itr_limit:
        return 1

    if stone == 0:
        return stone_blink(1, itr + 1, itr_limit)
    elif len(str(stone)) % 2 == 0:
        length = len(str(stone))
        return (stone_blink(int(str(stone)[:int(length / 2)]), itr + 1, itr_limit) +
                stone_blink(int(str(stone)[int(length / 2):]), itr + 1, itr_limit))
    else:
        return stone_blink(stone * 2024, itr + 1, itr_limit)


PuzzleRunner().run(part1, part2)
