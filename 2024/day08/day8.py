import itertools

from runner import PuzzleRunner


def part1(lines):
    grid = []
    antenna_map = {}
    for row_index, line in enumerate(lines):
        grid.append(line.strip())
        for col_index, val in enumerate(line.strip()):
            if val != '.':
                antenna_map.setdefault(val, []).append((col_index, row_index))
    antinodes = []
    for antenna in antenna_map:
        for combination in list(itertools.combinations(antenna_map[antenna], 2)):
            delta_y = abs((combination[0][1] - combination[1][1]))
            delta_x = abs((combination[0][0] - combination[1][0]))
            first = combination[0]
            second = combination[1]
            if first[0] <= second[0]:  # \
                coords = (first, second) if first[1] < second[1] else (second, first)
                add_if_not_outside_map(above_left(coords[0], delta_x, delta_y), grid, antinodes)
                add_if_not_outside_map(down_right(coords[1], delta_x, delta_y), grid, antinodes)
            else:  # /
                coords = (first, second) if first[1] < second[1] else (second, first)
                add_if_not_outside_map(above_right(coords[0], delta_x, delta_y), grid, antinodes)
                add_if_not_outside_map(down_left(coords[1], delta_x, delta_y), grid, antinodes)
    print(len(set(antinodes)))


def part2(lines):
    grid = []
    antenna_map = {}
    for row_index, line in enumerate(lines):
        grid.append(line.strip())
        for col_index, val in enumerate(line.strip()):
            if val != '.':
                antenna_map.setdefault(val, []).append((col_index, row_index))
    antinodes = []
    for antenna in antenna_map:
        for combination in list(itertools.combinations(antenna_map[antenna], 2)):
            delta_y = abs((combination[0][1] - combination[1][1]))
            delta_x = abs((combination[0][0] - combination[1][0]))
            first = combination[0]
            second = combination[1]
            antinodes.append(first)
            antinodes.append(second)
            if first[0] <= second[0]:  # \
                add_until_out_of_bounds(first, above_left, delta_x, delta_y, grid, antinodes)
                add_until_out_of_bounds(first, down_right, delta_x, delta_y, grid, antinodes)
            else:  # /
                add_until_out_of_bounds(first, above_right, delta_x, delta_y, grid, antinodes)
                add_until_out_of_bounds(first, down_left, delta_x, delta_y, grid, antinodes)
    print(len(set(antinodes)))


def above_left(curr, delta_x, delta_y):
    return (curr[0] - delta_x, curr[1] - delta_y)


def above_right(curr, delta_x, delta_y):
    return (curr[0] + delta_x, curr[1] - delta_y)


def down_right(curr, delta_x, delta_y):
    return (curr[0] + delta_x, curr[1] + delta_y)


def down_left(curr, delta_x, delta_y):
    return (curr[0] - delta_x, curr[1] + delta_y)


def add_until_out_of_bounds(start, coord_func, delta_x, delta_y, grid, antinodes):
    curr = start
    while True:
        curr = coord_func(curr, delta_x, delta_y)
        if not add_if_not_outside_map(curr, grid, antinodes):
            break


def add_if_not_outside_map(coord, grid, antinodes):
    max_x = len(grid[0].strip())
    max_y = len(grid)
    if 0 <= coord[0] < max_x and 0 <= coord[1] < max_y:
        antinodes.append(coord)
        return True
    return False


PuzzleRunner().run(part1, part2)
