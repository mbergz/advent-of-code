import sys

import numpy as np

input_file = open('day13.txt', 'r')
lines = input_file.readlines()


# brute force
def part1():
    score = 0
    for i in range(0, len(lines), 4):
        chunk = lines[i:i + 3]
        button_a = extract_x_y(chunk[0].strip(), "+")
        button_b = extract_x_y(chunk[1].strip(), "+")
        prize = extract_x_y(chunk[2].strip(), "=")
        smallest = sys.maxsize
        for nbr_a_press in range(1, 101):
            for nbr_b_press in range(1, 101):
                res = calc(nbr_a_press, nbr_b_press, button_a, button_b)
                if res == prize:
                    smallest = min(smallest, nbr_a_press * 3 + nbr_b_press)
        if smallest != sys.maxsize:
            score += smallest
    print(score)


# linear algebra
def part2():
    score = 0
    for i in range(0, len(lines), 4):
        chunk = lines[i:i + 3]
        a = extract_x_y(chunk[0].strip(), "+")
        b = extract_x_y(chunk[1].strip(), "+")
        prize = extract_prize_part2(chunk[2].strip())
        coefficients = np.array([[a[0], b[0]], [a[1], b[1]]])
        np_res = calc_np_round(coefficients, prize)
        if check_solution(coefficients, np_res, prize):
            score += int(np_res[0] * 3 + np_res[1])
    print(score)


def check_solution(coefficients, sol, prize):
    result = coefficients @ sol  # matrix multiplication
    return np.all(result == prize)


def calc(nbr_a_press, nbr_b_press, a, b):
    new_x = nbr_a_press * a[0] + nbr_b_press * b[0]
    new_y = nbr_a_press * a[1] + nbr_b_press * b[1]
    return new_x, new_y


def calc_np_round(coefficients, prize):
    results = np.array([prize[0], prize[1]])
    sol = np.linalg.solve(coefficients, results)
    return np.rint(sol)


def extract_x_y(line, sep):
    curr = line.strip().split(":")
    x = int(curr[1].split(",")[0].split(sep)[1])
    y = int(curr[1].split(",")[1].split(sep)[1])
    return x, y


def extract_prize_part2(line):
    curr = line.strip().split(":")
    x = int(curr[1].split(",")[0].split("=")[1]) + 10_000_000_000_000
    y = int(curr[1].split(",")[1].split("=")[1]) + 10_000_000_000_000
    return x, y


part1()
part2()
