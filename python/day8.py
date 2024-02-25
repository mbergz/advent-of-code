file = open('./../src/main/resources/day8.txt', 'r')
lines = file.readlines()

START_KEY = 'AAA'
END_KEY = 'ZZZ'


def create_network():
    network = {}
    for node_line in lines[2:]:
        main = node_line.split("=")[0].strip()
        left_right = node_line.split("=")[1][2:-2].split(", ")
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


print(part1())
