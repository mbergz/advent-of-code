file = open('./../src/main/resources/day5.txt', 'r')
lines = file.readlines()


def part1():
    seeds = list(map(int, lines[0].split(":")[1].split()))
    sections = read_sections()
    results = []
    for seed in seeds:
        new_seed = seed
        for section in sections:
            for range in section:
                if range['source'] <= new_seed <= range['source'] + range['diff'] - 1:
                    offset = range['source'] + (range['diff'] - 1) - new_seed
                    new_seed = (range['dest'] + (range['diff'] - 1)) - offset
                    break
        results.append(new_seed)
    return min(results)


def part2():
    sections = read_sections()
    results = []
    for seed in get_seeds_part2():
        recursive_divide_ranges(seed, results, sections)
    return min(results, key=lambda x: x['from'])['from']


def get_seeds_part2():
    res = []
    seeds = list(map(int, lines[0].split(":")[1].split()))
    for i in range(0, len(seeds), 2):
        res.append({'from': seeds[i], 'to': seeds[i] + seeds[i + 1] - 1})
    return res


def recursive_divide_ranges(curr_range, results, sections):
    new_final_range = is_range_valid(curr_range, sections)
    if new_final_range:
        results.append(new_final_range)
        return
    if curr_range['from'] == curr_range['to']:
        return
    new_ranges_to_test = divide_range(curr_range)
    recursive_divide_ranges(new_ranges_to_test[0], results, sections)
    recursive_divide_ranges(new_ranges_to_test[1], results, sections)


def divide_range(range_to_divide):
    mid_point = (range_to_divide['from'] + range_to_divide['to'] + 1) // 2
    new_range_a = {'from': range_to_divide['from'], 'to': mid_point - 1}
    new_range_b = {'from': mid_point, 'to': range_to_divide['to']}
    return [new_range_a, new_range_b]


def is_range_valid(range_to_test, sections):
    new_range_to_test = range_to_test
    for section in sections:
        for range in section:
            if range['source'] <= new_range_to_test['from'] <= range['source'] + range['diff'] - 1 and \
                    range['source'] <= new_range_to_test['to'] <= range['source'] + range['diff'] - 1:
                offset = range['source'] + (range['diff'] - 1) - new_range_to_test['from']
                new_seed_from = (range['dest'] + (range['diff'] - 1)) - offset
                old_range_offset = new_range_to_test['to'] - new_range_to_test['from']
                new_range_to_test = {'from': new_seed_from, 'to': new_seed_from + old_range_offset}
                break
            elif range['source'] <= new_range_to_test['from'] <= range['source'] + range['diff'] - 1 or \
                    range['source'] <= new_range_to_test['to'] <= range['source'] + range['diff'] - 1:
                return None
    return new_range_to_test


def read_sections():
    sections = []
    section = []
    for line in lines[1:]:
        if line and line[0].isdigit():
            section.append({'dest': int(line.split()[0]), 'source': int(line.split()[1]), 'diff': int(line.split()[2])})
        else:
            if section:
                sections.append(section)
                section = []
    sections.append(section)
    return sections


print(part1())
print(part2())
