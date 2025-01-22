from runner import PuzzleRunner

directions = [
    (0, 1),  # down
    (1, 0),  # right
    (-1, 0),  # left
    (0, -1)  # up
]

all_dirs_map = {
    "b": (0, 1),
    "r": (1, 0),
    "l": (-1, 0),
    "t": (0, -1),
    "tl": (-1, -1),
    "tr": (1, -1),
    "bl": (-1, 1),
    "br": (1, 1),
}


def part1(lines):
    all_plants, grid = initialize_grid_plants(lines)
    score = 0
    visited = []
    while len(all_plants) > 0:
        start = all_plants[-1]
        queue = [start]
        area = 0
        perimeter = 0
        while len(queue) > 0:
            curr = queue.pop()
            all_plants.pop(all_plants.index(curr))
            visited.append(curr)
            area += 1
            perimeter_score = 4
            for d in directions:
                neighbour = (curr[0] + d[0], curr[1] + d[1], curr[2])
                if is_matching_plant(grid, neighbour):
                    if neighbour not in visited and neighbour not in queue:
                        queue.append(neighbour)
                    perimeter_score -= 1
            perimeter += perimeter_score
        score += area * perimeter
    print(score)


def part2(lines):
    all_plants, grid = initialize_grid_plants(lines)
    score = 0
    visited = []
    while len(all_plants) > 0:
        start = all_plants[-1]
        queue = [start]
        group = []
        area = 0
        while len(queue) > 0:
            curr = queue.pop()
            group.append((curr[0], curr[1]))
            all_plants.pop(all_plants.index(curr))
            visited.append(curr)
            area += 1
            for d in directions:
                neighbour = (curr[0] + d[0], curr[1] + d[1], curr[2])
                if is_matching_plant(grid, neighbour):
                    if neighbour not in visited and neighbour not in queue:
                        queue.append(neighbour)
        score += area * count_edges(group)
    print(score)


def initialize_grid_plants(lines):
    grid = []
    all_plants = []
    for row_index, line in enumerate(lines):
        grid.append(line.strip())
        for col_index, val in enumerate(line.strip()):
            all_plants.append((col_index, row_index, val))
    return all_plants, grid


def count_edges(group):
    diags = 0
    found_corners = set()
    for node in group:
        # check top left
        top_left = (node[0] + all_dirs_map["tl"][0], node[1] + all_dirs_map["tl"][1])
        above = (node[0] + all_dirs_map["t"][0], node[1] + all_dirs_map["t"][1])
        top_right = (node[0] + all_dirs_map["tr"][0], node[1] + all_dirs_map["tr"][1])
        right = (node[0] + all_dirs_map["r"][0], node[1] + all_dirs_map["r"][1])
        bottom_right = (node[0] + all_dirs_map["br"][0], node[1] + all_dirs_map["br"][1])
        bottom = (node[0] + all_dirs_map["b"][0], node[1] + all_dirs_map["b"][1])
        bottom_left = (node[0] + all_dirs_map["bl"][0], node[1] + all_dirs_map["bl"][1])
        left = (node[0] + all_dirs_map["l"][0], node[1] + all_dirs_map["l"][1])

        if add_corner(top_left, [above, left], group, found_corners, top_left):
            diags += 1
        if add_corner(top_right, [above, right], group, found_corners, above):
            diags += 1
        if add_corner(bottom_right, [bottom, right], group, found_corners, node):
            diags += 1
        if add_corner(bottom_left, [bottom, left], group, found_corners, left):
            diags += 1
    return len(found_corners) + int(diags / 2)


# Returns True if diagonal is found
def add_corner(corner, others, group, found_edges, top_left):
    corner_exist = corner in group
    other_a_exist = others[0] in group
    other_b_exist = others[1] in group
    if corner_exist and other_a_exist and other_b_exist:
        return False
    if not corner_exist and ((other_a_exist and not other_b_exist) or (other_b_exist and not other_a_exist)) == 1:
        return False
    if corner_exist and not other_a_exist and not other_b_exist:
        found_edges.add(top_left)
        return True, True
    found_edges.add(top_left)
    return False


def is_matching_plant(grid, plant):
    if 0 <= plant[0] < len(grid[0]) and 0 <= plant[1] < len(grid):
        return plant[2] == grid[plant[1]][plant[0]]
    return False


PuzzleRunner().run(part1, part2)
