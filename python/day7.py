from functools import cmp_to_key

file = open('./../src/main/resources/day7.txt', 'r')
lines = file.readlines()

order_part1 = '23456789TJQKA'


def get_type(hand):
    char_count = {char: hand.count(char) for char in set(hand)}
    sorted_char_count = sorted(char_count.values())
    if max(char_count.values()) == 5:
        return 7
    if max(char_count.values()) == 4:
        return 6
    if sorted_char_count == [2, 3]:
        return 5
    if max(char_count.values()) == 3:
        return 4
    if sorted_char_count == [1, 2, 2]:
        return 3
    if sorted_char_count == [1, 1, 1, 2]:
        return 2
    return 1


def custom_compare(item1, item2):
    hand1 = item1["hand"]
    hand2 = item2["hand"]
    score1 = get_type(hand1)
    score2 = get_type(hand2)
    if score1 < score2:
        return 1
    if score1 > score2:
        return -1
    for i in range(len(hand1)):
        if order_part1.index(hand1[i]) < order_part1.index(hand2[i]):
            return 1
        if order_part1.index(hand1[i]) > order_part1.index(hand2[i]):
            return -1
    return 0


def part1():
    hands = [{"hand": line.split()[0], "bid": int(line.split()[1])} for line in lines]
    sorted_hands = sorted(hands, key=cmp_to_key(custom_compare))
    result = 0
    index = 0
    for i in range(len(sorted_hands), 0, -1):
        result += sorted_hands[index]["bid"] * i
        index += 1
    return result


print(part1())
