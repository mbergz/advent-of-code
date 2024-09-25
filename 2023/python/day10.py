import math

file = open('./../src/main/resources/day10.txt', 'r')
lines = file.readlines()

right = (1, 0)
down = (0, 1)
left = (-1, 0)
up = (0, -1)

possible_directions_map = {
    '-': [right, left],
    '|': [up, down],
    'L': [up, right],
    'J': [up, left],
    '7': [left, down],
    'F': [right, down]
}

get_inverse_direction_map = {
    right: left,
    left: right,
    down: up,
    up: down
}


def get_start_index(grid):
    for i, line in enumerate(grid):
        for j, char in enumerate(line):
            if char == 'S':
                return i, j


def get_new_direction(current_char, from_dir):
    dirs = possible_directions_map.get(current_char)
    if dirs[0] != from_dir:
        return dirs[0]
    return dirs[1]


def part1():
    grid = [line.strip() for line in lines]
    start_idx = get_start_index(grid)
    curr_y, curr_x = start_idx
    curr_x += 1
    direction = right
    from_direction = left
    steps = 1
    while True:
        curr_x, curr_y = (direction[0] + curr_x, direction[1] + curr_y)
        current_char = grid[curr_y][curr_x]
        if current_char == 'S':
            break
        direction = get_new_direction(current_char, from_direction)
        from_direction = get_inverse_direction_map.get(direction)
        steps += 1
    return math.ceil(steps / 2)


print(part1())
