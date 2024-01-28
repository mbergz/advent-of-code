import re

file1 = open('./../src/main/resources/day3.txt', 'r')
lines = file1.readlines()


def part1():
    valid_engine_parts = []
    for lineNbr, line in enumerate(lines):
        found_nbrs = [{'nbr': match.group(), 'index': match.start()} for match in re.finditer(r'\d+', line)]
        for found_nbr in found_nbrs:
            if lineNbr > 0 and check_line_above_or_below(lines[lineNbr - 1], found_nbr):
                valid_engine_parts.append(found_nbr['nbr'])
                continue
            if check_curr_line(line, found_nbr):
                valid_engine_parts.append(found_nbr['nbr'])
                continue
            if lineNbr < len(lines) - 1 and check_line_above_or_below(lines[lineNbr + 1], found_nbr):
                valid_engine_parts.append(found_nbr['nbr'])
                continue
    return sum([int(i) for i in valid_engine_parts])


def part2():
    result = 0
    for line_nbr, line in enumerate(lines):
        nbrs_curr_line = [{'nbr': match.group(), 'index': match.start()} for match in re.finditer(r'\d+', line)]
        nbrs_prev_line = [{'nbr': match.group(), 'index': match.start()} for match in
                          re.finditer(r'\d+', lines[line_nbr - 1])] if line_nbr > 0 else []
        nbrs_next_line = [{'nbr': match.group(), 'index': match.start()} for match in
                          re.finditer(r'\d+', lines[line_nbr + 1])] if line_nbr < len(lines) - 1 else []
        found_stars = [{'nbr': match.group(), 'index': match.start()} for match in re.finditer(r'\*', line)]
        for found_star in found_stars:
            found_adjacent_nbrs_to_star = []
            found_adjacent_nbrs_to_star += check_line_above_or_below_adjacent_star(found_star, nbrs_prev_line)
            found_adjacent_nbrs_to_star += check_curr_line_for_nbrs_adjacent_star(found_star, nbrs_curr_line)
            found_adjacent_nbrs_to_star += check_line_above_or_below_adjacent_star(found_star, nbrs_next_line)
            if len(found_adjacent_nbrs_to_star) == 2:
                result += (int(found_adjacent_nbrs_to_star[0]['nbr']) * int(found_adjacent_nbrs_to_star[1]['nbr']))
    return result


def check_curr_line_for_nbrs_adjacent_star(found_star, found_nbrs):
    found = []
    for nbr in found_nbrs:
        if get_end_index(nbr) + 1 == found_star['index'] or nbr['index'] - 1 == found_star['index']:
            found.append(nbr)
    return found


def check_line_above_or_below_adjacent_star(found_star, found_nbrs):
    found = []
    for nbr in found_nbrs:
        from_index = nbr['index']
        to_index = get_end_index(nbr)
        if from_index - 1 <= found_star['index'] <= to_index + 1:
            found.append(nbr)
    return found


def check_curr_line(line, found_nbr):
    start = get_start_minus_one(found_nbr)
    end = get_end_plus_one(line, found_nbr)
    return line[start] != '.' and not line[start].isdigit() or line[end] != '.' and not line[end].isdigit()


def check_line_above_or_below(line, found_nbr):
    for i in range(get_start_minus_one(found_nbr), get_end_plus_one(line, found_nbr) + 1):
        if line[i] != '.' and not line[i].isdigit():
            return True
    return False


def get_start_minus_one(found_nbr):
    return found_nbr['index'] - 1 if found_nbr['index'] > 0 else 0


def get_end_plus_one(line, found_nbr):
    index = found_nbr['index']
    nbr = found_nbr['nbr']
    return index + len(nbr) if index + len(nbr) < len(line) - 1 else index + len(nbr) - 1


def get_end_index(found_nbr):
    return found_nbr['index'] + len(found_nbr['nbr']) - 1


print(part1())
print(part2())
