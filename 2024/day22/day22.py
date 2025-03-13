import itertools
import math
from collections import deque

from runner import PuzzleRunner


def part1(lines):
    res = 0
    for secret in map(int, lines):
        for _ in range(2000):
            secret = calc_secret(secret)
        res += secret
    print(res)


def part2(lines):
    """
    Brute force by building up map of all sequences and their respective prices.
    Then loop all possibilities of sequences (-9,-9,-9,-9) -> (9,9,9,9) and test the max for all.
    """
    seq_prices = []
    for secret in map(int, lines):
        seq_prices.append(get_seq_price_dict(secret))

    total = 0
    for combo in itertools.product(range(-9, 10), repeat=4):
        res = 0
        for seq_price in seq_prices:
            if combo not in seq_price:
                continue
            res += seq_price[combo]
        total = max(total, res)

    print(total)


def get_seq_price_dict(secret):
    res = {}
    seq = deque(maxlen=4)
    prev_price = secret % 10
    secret = calc_secret(secret)  # dont include first elem
    for i in range(1999):
        price = secret % 10
        seq.append(price - prev_price)
        prev_price = price
        if i >= 3:
            if tuple(seq) not in res:
                res[tuple(seq)] = secret % 10
        secret = calc_secret(secret)
    return res


def calc_secret(secret):
    secret = ((secret * 64) ^ secret) % 16777216
    secret = (math.floor(secret / 32) ^ secret) % 16777216
    secret = ((secret * 2048) ^ secret) % 16777216
    return secret


PuzzleRunner().run(part1, part2)
