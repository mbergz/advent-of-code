import math

file = open('./../src/main/resources/day8.txt', 'r')
lines = file.readlines()

START_KEY = 'AAA'
END_KEY = 'ZZZ'


def create_network():
    network = {}
    for node_line in lines[2:]:
        main = node_line.split("=")[0].strip()
        left_right = node_line.split("=")[1].strip()[1:-1].split(", ")
        network[main] = left_right
    return network


def part1():
    instructions = lines[0].strip()
    network = create_network()
    nbr_of_steps = 0
    current_key = START_KEY
    while True:
        for instruction in instructions:
            nbr_of_steps += 1
            current_key = network[current_key][0 if instruction == 'L' else 1]
            if current_key == END_KEY:
                return nbr_of_steps


def steps_to_first_ending_z(start_key, instructions, network):
    nbr_of_steps = 0
    current_key = start_key
    while True:
        for instruction in instructions:
            nbr_of_steps += 1
            current_key = network[current_key][0 if instruction == 'L' else 1]
            if current_key[2] == 'Z':
                return nbr_of_steps


def part2():
    instructions = lines[0].strip()
    network = create_network()
    starting_keys = [key for key in network if key.endswith('A')]
    all_steps_to_first_z = [steps_to_first_ending_z(start_key, instructions, network) for start_key in starting_keys]
    return math.lcm(*all_steps_to_first_z)


print(part1())
print(part2())
