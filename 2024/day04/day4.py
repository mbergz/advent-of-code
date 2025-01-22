from runner import PuzzleRunner


def part1(lines):
    grid = []
    for line in lines:
        grid.append(line.strip())
    count = 0
    for y, row in enumerate(grid):
        for x, val in enumerate(row):
            if val == 'X':
                count += find_pattern(x, y, 'XMAS', grid)
            elif val == 'S':
                count += find_pattern(x, y, 'SAMX', grid)
    print(count)


def part2(lines):
    grid = []
    for line in lines:
        grid.append(line.strip())
    count = 0
    for y in range(1, len(grid) - 1):
        for x in range(1, len(grid[y]) - 1):
            if grid[y][x] == 'A':
                count += find_x_mas(x, y, grid)
    print(count)


def find_pattern(x, y, pattern, grid):
    found = 0
    if x + 3 < len(grid[0]) and grid[y][x:x + 4] == pattern:
        found += 1
    # |
    if y + 3 < len(grid) and (
            grid[y][x] + grid[y + 1][x] + grid[y + 2][x] + grid[y + 3][x] == pattern):
        found += 1
    # /
    if x - 3 >= 0 and y + 3 < len(grid) and (
            grid[y][x] + grid[y + 1][x - 1] + grid[y + 2][x - 2] + grid[y + 3][x - 3] == pattern):
        found += 1
    # \
    if x + 3 < len(grid[0]) and y + 3 < len(grid) and (
            grid[y][x] + grid[y + 1][x + 1] + grid[y + 2][x + 2] + grid[y + 3][x + 3] == pattern):
        found += 1
    return found


def find_x_mas(x, y, grid):
    if (
            ((grid[y - 1][x - 1] == 'M' and grid[y + 1][x + 1] == 'S') or
             (grid[y - 1][x - 1] == 'S' and grid[y + 1][x + 1] == 'M'))
            and
            ((grid[y - 1][x + 1] == 'M' and grid[y + 1][x - 1] == 'S') or
             (grid[y - 1][x + 1] == 'S' and grid[y + 1][x - 1] == 'M'))
    ):
        return 1
    return 0


PuzzleRunner().run(part1, part2)
