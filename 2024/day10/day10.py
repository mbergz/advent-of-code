input_file = open('day10.txt', 'r')
lines = input_file.readlines()

directions = [
    (0, 1),
    (1, 0),
    (-1, 0),
    (0, -1)
]


def part1():
    grid = []
    for line in lines:
        grid.append(line.strip())
    total_score = 0
    for head in find_trail_heads(grid):
        total_score += len(dfs(head, grid, []))
    print(total_score)


def part2():
    grid = []
    for line in lines:
        grid.append(line.strip())
    total_score = 0
    for head in find_trail_heads(grid):
        total_score += len(dfs_part2(head, grid, []))
    print(total_score)


def find_trail_heads(grid):
    res = []
    for rowI, row in enumerate(grid):
        for colI, col in enumerate(row):
            if col == "0":
                res.append((colI, rowI))
    return res


def dfs(coord, grid, path):
    path = path + [coord]
    found = set()

    if grid[coord[1]][coord[0]] == "9":
        found.add(coord)

    for next_coord in get_next_coords(coord, grid):
        if next_coord not in path:
            found = found.union(dfs(next_coord, grid, path))

    return found


def dfs_part2(coord, grid, path):
    path = path + [coord]
    paths = []

    if grid[coord[1]][coord[0]] == "9":
        paths.append(path)

    for next_coord in get_next_coords(coord, grid):
        if next_coord not in path:
            res = dfs_part2(next_coord, grid, path)
            if res:
                paths.extend(res)

    return paths


def get_next_coords(current, grid):
    current_nbr = int(grid[current[1]][current[0]])
    res = []
    for d in directions:
        new_coord = current[0] + d[0], current[1] + d[1]
        if new_coord[0] < 0 or new_coord[0] >= len(grid[0]) or new_coord[1] < 0 or new_coord[1] >= len(grid):
            continue
        if int(grid[new_coord[1]][new_coord[0]]) == current_nbr + 1:
            res.append(new_coord)
    return res


part1()
part2()
