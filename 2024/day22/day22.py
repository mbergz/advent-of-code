import math

from runner import PuzzleRunner


def part1(lines):
    res = 0
    for secret in map(int, lines):
        for _ in range(2000):
            secret = calc_secret(secret)
        res += secret
    print(res)


def calc_secret(secret):
    secret = ((secret * 64) ^ secret) % 16777216
    secret = (math.floor(secret / 32) ^ secret) % 16777216
    secret = ((secret * 2048) ^ secret) % 16777216
    return secret


PuzzleRunner().run(part1)
