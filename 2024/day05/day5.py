from functools import cmp_to_key

input_file = open('day5.txt', 'r')
lines = input_file.readlines()


def part1():
    rules = lines[0: lines.index("\n")]
    updates = lines[lines.index("\n") + 1:len(lines)]

    score = 0
    for update in updates:
        update_line = update.strip().split(",")
        updated_first_map = get_updated_first_map(update_line, rules)
        if is_valid(update_line, updated_first_map):
            score += int(update_line[int(len(update_line) / 2)])
    print(score)


def part2():
    rules = lines[0: lines.index("\n")]
    updates = lines[lines.index("\n") + 1:len(lines)]

    score = 0
    for update in updates:
        update_line = update.strip().split(",")
        updated_first_map = get_updated_first_map(update_line, rules)
        if not is_valid(update_line, updated_first_map):
            score += fix_invalid_line(update_line, updated_first_map)
    print(score)


def is_valid(update_line, updated_first_map):
    validated = []
    for nbr in update_line:
        if nbr in updated_first_map:
            if any(i not in validated for i in updated_first_map[nbr]):
                return False
        validated.append(nbr)
    return True


def get_updated_first_map(update_list, rules):
    updated_first_map = {}

    for rule in rules:
        parts = rule.strip().split("|")
        if all(u in update_list for u in parts):
            updated_first_map.setdefault(parts[1], []).append(parts[0])

    return updated_first_map


def fix_invalid_line(update_line, updated_first_map):
    def comp_fn(nbr1, nbr2):
        if nbr1 in updated_first_map and nbr2 in updated_first_map[nbr1]:
            return 1  # nbr2 before nbr1
        if nbr2 in updated_first_map and nbr1 in updated_first_map[nbr2]:
            return -1  # nbr 1 before nbr2
        return 0

    sorted_list = sorted(update_line, key=cmp_to_key(comp_fn))
    return int(sorted_list[int(len(sorted_list) / 2)])


part1()
part2()
