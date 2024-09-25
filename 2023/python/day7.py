from functools import cmp_to_key

file = open('./../src/main/resources/day7.txt', 'r')
lines = file.readlines()

order_part1 = '23456789TJQKA'
order_part2 = 'J23456789TQKA'


def get_type_part1(hand):
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


def get_type_part2(hand):
    char_count = {char: hand.count(char) for char in set(hand) if char != 'J'}
    sorted_values = sorted(char_count.values(), reverse=True)
    nbr_jokers = hand.count('J')
    if not sorted_values or sorted_values[0] + nbr_jokers == 5:
        return 7
    if sorted_values[0] + nbr_jokers == 4:
        return 6
    if sorted_values[0] + nbr_jokers == 3 and sorted_values[1] == 2:
        return 5
    if sorted_values[0] + nbr_jokers == 3:
        return 4
    if sorted_values[0] == 2 and (nbr_jokers or sorted_values[1] == 2):
        return 3
    if sorted_values[0] + nbr_jokers == 2:
        return 2
    return 1


def custom_compare_part1(item1, item2):
    hand1 = item1["hand"]
    hand2 = item2["hand"]
    score1 = get_type_part1(hand1)
    score2 = get_type_part1(hand2)
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


def custom_compare_part2(item1, item2):
    hand1 = item1["hand"]
    hand2 = item2["hand"]
    score1 = get_type_part2(hand1)
    score2 = get_type_part2(hand2)
    if score1 < score2:
        return 1
    if score1 > score2:
        return -1
    for i in range(len(hand1)):
        if order_part2.index(hand1[i]) < order_part2.index(hand2[i]):
            return 1
        if order_part2.index(hand1[i]) > order_part2.index(hand2[i]):
            return -1
    return 0


def part1():
    hands = [{"hand": line.split()[0], "bid": int(line.split()[1])} for line in lines]
    sorted_hands = sorted(hands, key=cmp_to_key(custom_compare_part1))
    result = 0
    index = 0
    for i in range(len(sorted_hands), 0, -1):
        result += sorted_hands[index]["bid"] * i
        index += 1
    return result


def part2():
    hands = [{"hand": line.split()[0], "bid": int(line.split()[1])} for line in lines]
    sorted_hands = sorted(hands, key=cmp_to_key(custom_compare_part2))
    result = 0
    index = 0
    for i in range(len(sorted_hands), 0, -1):
        result += sorted_hands[index]["bid"] * i
        index += 1
    return result


print(part1())
print(part2())
