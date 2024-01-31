file1 = open('./../src/main/resources/day4.txt', 'r')
lines = file1.readlines()


def part1():
    score = 0
    for line in lines:
        res = line.split('|')
        winning_nbrs = set(res[0].split(':')[1].strip().split(' '))
        actual_nbrs = set(res[1].strip().split())
        line_score = 0
        for winning_nbr in winning_nbrs:
            if winning_nbr in actual_nbrs:
                line_score = line_score * 2 if line_score != 0 else 1
        score += line_score
    return score


def part2():
    score = 0
    scratch_cards = [{'index': index, 'line': line, 'copies': 1} for index, line in enumerate(lines)]
    for card in scratch_cards:
        res = card['line'].split('|')
        winning_nbrs = set(res[0].split(':')[1].strip().split(' '))
        actual_nbrs = set(res[1].strip().split())
        line_score = len(winning_nbrs.intersection(actual_nbrs))
        i = card['index']
        for _ in range(line_score):
            i += 1
            scratch_cards[i]['copies'] += card['copies']
        score += card['copies']
    return score


print(part1())
print(part2())
