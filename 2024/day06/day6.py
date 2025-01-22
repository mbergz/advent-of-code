from runner import PuzzleRunner

direction_map = {
    0: (0, -1),  # up
    1: (1, 0),  # right
    2: (0, 1),  # down
    3: (-1, 0),  # left
}

turn_90_right_map = {
    0: 1,
    1: 2,
    2: 3,
    3: 0
}


def part1(lines):
    grid = []
    for line in lines:
        grid.append(line.strip())

    visited = set()
    x, y = find_start(grid)
    dir = 0

    while True:
        new_x, new_y = x + direction_map[dir][0], y + direction_map[dir][1]
        if new_x < 0 or new_x >= len(grid[0]) or new_y < 0 or new_y >= len(grid):
            break
        while grid[new_y][new_x] == '#':
            dir = turn_90_right_map[dir]
            new_x, new_y = x + direction_map[dir][0], y + direction_map[dir][1]
        x, y = new_x, new_y
        visited.add((x, y))
    print(len(visited))


def part2(lines):
    orig_grid = []
    for line in lines:
        orig_grid.append(line.strip())

    count = 0
    for y, row in enumerate(orig_grid):
        for x, val in enumerate(row):
            if val == '^' or val == '#':
                continue
            grid = orig_grid[:]
            grid[y] = grid[y][:x] + '#' + grid[y][x + 1:]
            if is_stuck_loop(grid):
                count += 1
    print(count)


def is_stuck_loop(grid):
    x, y = find_start(grid)
    visited = set()
    dir = 0

    while True:
        new_x, new_y = x + direction_map[dir][0], y + direction_map[dir][1]
        if new_x < 0 or new_x >= len(grid[0]) or new_y < 0 or new_y >= len(grid):
            return False
        while grid[new_y][new_x] == '#':
            dir = turn_90_right_map[dir]
            new_x, new_y = x + direction_map[dir][0], y + direction_map[dir][1]
        x, y = new_x, new_y
        if (x, y, dir) in visited:
            return True
        visited.add((x, y, dir))


def find_start(grid):
    for y, row in enumerate(grid):
        for x, val in enumerate(row):
            if val == '^':
                return x, y


PuzzleRunner().run(part1, part2)
