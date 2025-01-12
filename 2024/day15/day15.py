input_file = open('day15.txt', 'r')
lines = input_file.readlines()

direction_map = {
    "^": (0, -1),  # up
    ">": (1, 0),  # right
    "v": (0, 1),  # down
    "<": (-1, 0),  # left
}


def move(curr, direction, grid):
    new_curr = (curr[0] + direction_map[direction][0], curr[1] + direction_map[direction][1])
    next_pos = grid[new_curr[1]][new_curr[0]]
    if next_pos == "#":
        return curr
    if next_pos == "O":
        if direction in ["<", ">"]:
            end_i = new_curr[0]
            step = -1 if direction == "<" else 1
            while grid[new_curr[1]][end_i + step] == "O":
                end_i += step
            if grid[new_curr[1]][end_i + step] == ".":
                grid[new_curr[1]][new_curr[0]] = "@"
                grid[curr[1]][curr[0]] = "."
                grid[new_curr[1]][end_i + step] = "O"
                return new_curr[0], new_curr[1]
            return curr
        else:
            end_i = new_curr[1]
            step = -1 if direction == "^" else 1
            while grid[end_i + step][new_curr[0]] == "O":
                end_i += step
            if grid[end_i + step][new_curr[0]] == ".":
                grid[new_curr[1]][new_curr[0]] = "@"
                grid[curr[1]][curr[0]] = "."
                grid[end_i + step][new_curr[0]] = "O"
                return new_curr[0], new_curr[1]
            return curr
    if next_pos == ".":
        grid[new_curr[1]][new_curr[0]] = "@"
        grid[curr[1]][curr[0]] = "."
        return new_curr[0], new_curr[1]


def part1():
    grid, movements = parse_input()
    curr = find_start(grid)

    # print_grid(grid)
    for mov in movements:
        curr = move(curr, mov, grid)
        # print_grid(grid)

    result = 0
    for i, row in enumerate(grid):
        for j, col in enumerate(row):
            if col == "O":
                result += (100 * i) + j
    print(result)


def print_grid(grid):
    print()
    for i, row in enumerate(grid):
        for j, col in enumerate(row):
            print(col, end="")
        print()


def find_start(grid):
    for i, row in enumerate(grid):
        for j, col in enumerate(row):
            if col == "@":
                return j, i


def parse_input():
    grid = []
    movements = ""
    read_map = True
    for row_index, line in enumerate(lines):
        if line == "\n":
            read_map = False
            continue
        if read_map:
            grid.append(list(line.strip()))
        else:
            movements += line.strip()
    return grid, movements


def part2():
    print("ok")


part1()
part2()
