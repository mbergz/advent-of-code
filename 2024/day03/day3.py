import re

input_file = open('day3.txt', 'r')
lines = input_file.readlines()


def part1():
    one_line = ''.join(line.strip() for line in lines)
    matches = re.findall("mul\(([0-9]{1,3},[0-9]{1,3})\)", one_line)
    score = sum(int(m.split(",")[0]) * int(m.split(",")[1]) for m in matches)
    print(score)


def part2():
    one_line = ''.join(line.strip() for line in lines)
    mul_matches = list(re.finditer("mul\(([0-9]{1,3},[0-9]{1,3})\)", one_line))
    conditional_matches = list(re.finditer(r"do\(\)|don't\(\)", one_line))
    enabled = True
    score = 0
    for mul_match in mul_matches:
        conditional_matches = get_new_conditional_matches(mul_match.start(), conditional_matches)
        enabled = get_new_enabled(mul_match.start(), conditional_matches, enabled)
        if enabled:
            score += int(mul_match.group(1).split(",")[0]) * int(mul_match.group(1).split(",")[1])
    print(score)


def get_new_enabled(index, conditional_matches, current_enabled):
    if conditional_matches[0].start() >= index:
        return current_enabled
    return conditional_matches[0].group() == 'do()'


# Cut off conditional matches list until previous instruction for index
def get_new_conditional_matches(index, conditional_matches):
    found = 0
    for i, cm in enumerate(conditional_matches):
        if cm.start() < index:
            found = i
        else:
            break
    return conditional_matches[found:]


part1()
part2()
