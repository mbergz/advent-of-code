from collections import deque

from runner import PuzzleRunner

MEM_SPACE_WIDTH = 71
MEM_SPACE_HEIGHT = 71

directions = [
    (0, 1),  # down
    (1, 0),  # right
    (-1, 0),  # left
    (0, -1)  # up
]


def part1(lines):
    run(lines, 1024)


def part2(lines):
    blocks = 1025

    while True:
        if not run(lines, blocks, part2=True):
            coords = lines[blocks - 1].strip().split(",")
            print(f"{int(coords[0])},{int(coords[1])}")
            break
        blocks += 1


def get_next(curr, grid):
    res = []
    for d in directions:
        next_coord = (curr[0] + d[0], curr[1] + d[1])
        if (0 <= next_coord[1] < MEM_SPACE_HEIGHT and 0 <= next_coord[0] < MEM_SPACE_WIDTH and
                grid[next_coord[1]][next_coord[0]] == "."):
            res.append(next_coord)
    return res


def run(lines, blocks, part2=False):
    coords = [tuple(int(val) for val in x.strip().split(",")) for x in lines[:blocks]]
    grid = []
    for row in range(MEM_SPACE_HEIGHT):
        grid.append(list("." * MEM_SPACE_WIDTH))
    for coord in coords:
        grid[coord[1]][coord[0]] = "#"

    start = (0, 0)
    end = (MEM_SPACE_WIDTH - 1, MEM_SPACE_HEIGHT - 1)
    queue = deque([start])
    visited = set(start)

    steps = 0
    while len(queue) > 0:
        for _ in range(len(queue)):
            curr = queue.popleft()
            if curr == end:
                if not part2:
                    print(steps)
                return True

            for n in get_next(curr, grid):
                if n not in visited:
                    visited.add(n)
                    queue.append(n)
        steps += 1
    return False


PuzzleRunner().run(part1, part2)
