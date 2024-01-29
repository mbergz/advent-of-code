file1 = open('./../src/main/resources/day4.txt', 'r')
lines = file1.readlines()


def part1():
    score = 0
    for line in lines:
        res = line.split("|")
        winning_nbrs = set(res[0].split(":")[1].strip().split(" "))
        actual_nbrs = set(res[1].strip().split())
        line_score = 0
        for winning_nbr in winning_nbrs:
            if winning_nbr in actual_nbrs:
                line_score = line_score * 2 if line_score != 0 else 1
        score += line_score
    return score


print(part1())
