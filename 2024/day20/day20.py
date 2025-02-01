from runner import PuzzleRunner

directions = [
    (0, 1),
    (1, 0),
    (-1, 0),
    (0, -1)
]


def part1(lines):
    grid = []
    for line in lines:
        grid.append(line.strip())

    # Run race backwards to build up map of distances to goal
    goal = find_char(grid, "E")

    visited = {goal}
    curr = goal
    steps = 0

    distances = {goal: 0}

    while grid[curr[1]][curr[0]] != "S":
        for d in directions:
            new_coord = (curr[0] + d[0], curr[1] + d[1])
            if new_coord not in visited and grid[new_coord[1]][new_coord[0]] != "#":
                visited.add(new_coord)
                curr = new_coord
                steps += 1
                distances[new_coord] = steps
                break

    race_steps = steps

    cheat_distances = build_cheat_distances(distances, grid)
    print(sum(x <= race_steps - 100 for x in cheat_distances))


def build_cheat_distances(distances, grid):
    start = find_char(grid, "S")
    visited = {start}
    curr = start
    cheat_distances = []
    steps = 0

    while grid[curr[1]][curr[0]] != "E":
        next_coord = None
        for d in directions:
            new_coord = (curr[0] + d[0], curr[1] + d[1])
            if new_coord in visited:
                continue
            if grid[new_coord[1]][new_coord[0]] == "#":
                # Test if possible to cheat here
                new_coord_cheat = (new_coord[0] + d[0], new_coord[1] + d[1])
                if new_coord_cheat not in visited and is_within_bounds(new_coord_cheat, grid) and \
                        grid[new_coord_cheat[1]][new_coord_cheat[0]] != "#" and \
                        new_coord_cheat in distances:
                    cheat_distances.append(steps + 2 + distances[new_coord_cheat])
            else:
                # Go forward with race trac as usual
                visited.add(new_coord)
                next_coord = new_coord
        curr = next_coord
        steps += 1

    return cheat_distances


def is_within_bounds(coord, grid):
    return 0 < coord[0] < len(grid[0]) and 0 < coord[1] < len(grid)


def find_char(grid, c):
    for row_i, row in enumerate(grid):
        for col_i, val in enumerate(row):
            if val == c:
                return col_i, row_i


PuzzleRunner().run(part1)
