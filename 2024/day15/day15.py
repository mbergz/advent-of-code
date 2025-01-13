input_file = open('day15.txt', 'r')
lines = input_file.readlines()

direction_map = {
    "^": (0, -1),  # up
    ">": (1, 0),  # right
    "v": (0, 1),  # down
    "<": (-1, 0),  # left
}


def part1():
    grid, movements = parse_input()
    curr = find_start(grid)
    for mov in movements:
        curr = move(curr, mov, grid)
    result = 0
    for i, row in enumerate(grid):
        for j, col in enumerate(row):
            if col == "O":
                result += (100 * i) + j
    print(result)


def part2():
    grid, movements = parse_input_part2()
    curr = find_start(grid)
    blocks = find_all_blocks(grid)
    # print_grid(grid, blocks, 0)
    for i, mov in enumerate(movements):
        curr = move_part2(curr, mov, grid, blocks)
        # print_grid(grid, blocks, i)
    #print_grid(grid, blocks, 0)
    result = sum((100 * b.left[1]) + b.left[0] for b in blocks)
    print(result)


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
                update_marker_grid(grid, curr, new_curr)
                grid[new_curr[1]][end_i + step] = "O"
                return new_curr[0], new_curr[1]
            return curr
        else:
            end_i = new_curr[1]
            step = -1 if direction == "^" else 1
            while grid[end_i + step][new_curr[0]] == "O":
                end_i += step
            if grid[end_i + step][new_curr[0]] == ".":
                update_marker_grid(grid, curr, new_curr)
                grid[end_i + step][new_curr[0]] = "O"
                return new_curr[0], new_curr[1]
            return curr
    if next_pos == ".":
        update_marker_grid(grid, curr, new_curr)
        return new_curr[0], new_curr[1]


class Block:
    def __init__(self, left: tuple[int, int], right: tuple[int, int]):
        self.left = left
        self.right = right

    def can_move(self, direction, grid):
        next_left = (self.left[0] + direction_map[direction][0], self.left[1] + direction_map[direction][1])
        next_right = (self.right[0] + direction_map[direction][0], self.right[1] + direction_map[direction][1])
        return grid[next_left[1]][next_left[0]] != "#" and grid[next_right[1]][next_right[0]] != "#"

    def move(self, direction):
        self.left = (self.left[0] + direction_map[direction][0], self.left[1] + direction_map[direction][1])
        self.right = (self.right[0] + direction_map[direction][0], self.right[1] + direction_map[direction][1])

    def push(self, blocks, grid, direction):
        if not self.can_move(direction, grid):
            return False

        neighbours = self.find_neighbours(blocks, direction)
        for n in neighbours:
            if not n.can_move(direction, grid):
                return False

        self.move(direction)
        for n in neighbours:
            n.move(direction)
        return True

    def find_neighbours(self, blocks, direction):
        next_left = (self.left[0] + direction_map[direction][0], self.left[1] + direction_map[direction][1])
        next_right = (self.right[0] + direction_map[direction][0], self.right[1] + direction_map[direction][1])
        found = set()
        for b in blocks:
            if (b.left in [next_left, next_right] or b.right in [next_left, next_right]) and b is not self:
                found.add(b)
        for f in list(found):
            found.update(f.find_neighbours(blocks, direction))
        return found


def move_part2(curr, direction, grid, blocks):
    new_curr = (curr[0] + direction_map[direction][0], curr[1] + direction_map[direction][1])
    next_pos = grid[new_curr[1]][new_curr[0]]
    if next_pos == "#":
        return curr
    found_block = next((b for b in blocks if b.left == new_curr or b.right == new_curr), None)
    if found_block:
        pushed = found_block.push(blocks, grid, direction)
        if pushed:
            update_marker_grid(grid, curr, new_curr)
            return new_curr[0], new_curr[1]
        return curr
    # "."
    update_marker_grid(grid, curr, new_curr)
    return new_curr[0], new_curr[1]


def update_marker_grid(grid, curr, new_curr):
    grid[new_curr[1]][new_curr[0]] = "@"
    grid[curr[1]][curr[0]] = "."


def print_grid(grid, blocks, index):
    print("Index: " + str(index))
    block_positions = {b.left for b in blocks} | {b.right for b in blocks}
    for i, row in enumerate(grid):
        j = 0
        while j < len(row) - 1:
            if (j, i) in block_positions:
                print("[]", end="")
                j += 2
            else:
                cell = row[j]
                print("." if cell != "#" and cell != "@" else cell, end="")
                j += 1
        print()


def find_start(grid):
    for i, row in enumerate(grid):
        for j, col in enumerate(row):
            if col == "@":
                return j, i


def find_all_blocks(grid):
    blocks = []
    for i, row in enumerate(grid):
        for j, col in enumerate(row):
            if col == "[":
                blocks.append(Block((j, i), (j + 1, i)))
    return blocks


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


def parse_input_part2():
    grid = []
    movements = ""
    read_map = True
    for row_index, line in enumerate(lines):
        if line == "\n":
            read_map = False
            continue
        if read_map:
            row = []
            for c in line.strip():
                if c == "#":
                    row.extend("##")
                if c == ".":
                    row.extend("..")
                if c == "@":
                    row.extend("@.")
                if c == "O":
                    row.extend("[]")
            grid.append(row)
        else:
            movements += line.strip()
    return grid, movements


part1()
part2()
