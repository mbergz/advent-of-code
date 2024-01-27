import re

file1 = open('./../src/main/resources/day3.txt', 'r')
lines = file1.readlines()


def part1():
    valid_engine_parts = []
    for lineNbr, line in enumerate(lines):
        found_nbrs = [{'nbr': match.group(), 'startIndex': match.start()} for match in re.finditer(r'\d+', line)]
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


def check_curr_line(line, found_nbr):
    start = get_start(found_nbr)
    end = get_end(line, found_nbr)
    return line[start] != '.' and not line[start].isdigit() or line[end] != '.' and not line[end].isdigit()


def check_line_above_or_below(line, found_nbr):
    for i in range(get_start(found_nbr), get_end(line, found_nbr) + 1):
        if line[i] != '.' and not line[i].isdigit():
            return True
    return False


def get_start(found_nbr):
    return found_nbr['startIndex'] - 1 if found_nbr['startIndex'] > 0 else 0


def get_end(line, found_nbr):
    index = found_nbr['startIndex']
    nbr = found_nbr['nbr']
    return index + len(nbr) if index + len(nbr) < len(line) - 1 else index + len(nbr) - 1


print(part1())
