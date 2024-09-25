import math
import re

file = open('./../src/main/resources/day6.txt', 'r')
lines = file.readlines()


def part1():
    time_limits = [int(x) for x in lines[0].split(":")[1].split()]
    record_distances = [int(x) for x in lines[1].split(":")[1].split()]
    res = []
    for index, time_limit in enumerate(time_limits):
        res.append(get_nbrs_of_ways_to_win(time_limit, record_distances[index]))
    return math.prod(res)


def part2():
    time_limit = int(''.join(re.findall(r'\d+', lines[0].split(":")[1])))
    record_distance = int(''.join(re.findall(r'\d+', lines[1].split(":")[1])))
    return get_nbrs_of_ways_to_win(time_limit, record_distance)


def get_nbrs_of_ways_to_win(time_limit, record_distance):
    sqrt = math.sqrt((time_limit / 2) ** 2 - record_distance)
    x1 = time_limit / 2 + sqrt
    x2 = time_limit / 2 - sqrt
    return int(x1) - int(x2)


print(part1())
print(part2())
