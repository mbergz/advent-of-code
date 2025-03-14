import math
from collections import deque, defaultdict

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
    Merge together into one single map with aggregated values for sequences.
    Then loop all possibilities valid of sequences and test the max for all.
    """
    aggregated_price_lookup = defaultdict(int)
    for secret in map(int, lines):
        seq_price_dict_buyer = get_seq_price_dict(secret)
        for key, value in seq_price_dict_buyer.items():
            aggregated_price_lookup[key] += value

    total = 0
    for combo in generate_sequences():
        if combo in aggregated_price_lookup:
            total = max(total, aggregated_price_lookup[combo])
    print(total)


def generate_sequences(prefix=(), last=0):
    if len(prefix) == 4:
        return [prefix]

    start = -9 if last >= 0 else -9 - last
    end = 9 if last <= 0 else 9 - last

    res = []
    for next_nbr in range(start, end + 1):
        res.extend(generate_sequences(prefix + (next_nbr,), next_nbr))
    return res


def get_seq_price_dict(secret):
    res = {}
    seq = deque(maxlen=4)
    prev_price = secret % 10
    secret = calc_secret(secret)  # don't include first elem

    for i in range(1999):
        price = secret % 10
        seq.append(price - prev_price)
        prev_price = price
        if i >= 3:
            res.setdefault(tuple(seq), secret % 10)
        secret = calc_secret(secret)

    return res


def calc_secret(secret):
    secret = ((secret * 64) ^ secret) % 16777216
    secret = (math.floor(secret / 32) ^ secret) % 16777216
    secret = ((secret * 2048) ^ secret) % 16777216
    return secret


PuzzleRunner().run(part1, part2)
