import sys

input_file = open('day13.txt', 'r')
lines = input_file.readlines()


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


def calc(nbr_a_press, nbr_b_press, a, b):
    new_x = nbr_a_press * a[0] + nbr_b_press * b[0]
    new_y = nbr_a_press * a[1] + nbr_b_press * b[1]
    return new_x, new_y


def extract_x_y(line, sep):
    curr = line.strip().split(":")
    x = int(curr[1].split(",")[0].split(sep)[1])
    y = int(curr[1].split(",")[1].split(sep)[1])
    return x, y


def part2():
    print("score")


part1()
part2()
