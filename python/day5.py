file1 = open('./../src/main/resources/day5.txt', 'r')
lines = file1.readlines()


def part1():
    seeds = list(map(int, lines.pop(0).split(":")[1].split()))
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


def read_sections():
    sections = []
    section = []
    for line in lines:
        if line and line[0].isdigit():
            section.append({'dest': int(line.split()[0]), 'source': int(line.split()[1]), 'diff': int(line.split()[2])})
        else:
            if section:
                sections.append(section)
                section = []
    sections.append(section)
    return sections


print(part1())
