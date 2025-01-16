import sys
from collections import deque
from dataclasses import dataclass
from typing import List, Tuple

input_file = open('day16.txt', 'r')
lines = input_file.readlines()

direction_map = {
    "^": (0, -1),  # up
    ">": (1, 0),  # right
    "v": (0, 1),  # down
    "<": (-1, 0),  # left
}

rotation_clockwise_map = {
    "^": ">",
    ">": "v",
    "v": "<",
    "<": "^",
}

rotation_counter_clockwise_map = {
    "^": "<",
    ">": "^",
    "v": ">",
    "<": "v",
}


@dataclass
class Node:
    x: int
    y: int
    direction: str
    score: int
    visited: List[Tuple[int, int]]


def solve():
    grid = []
    for line in lines:
        grid.append(line.strip())
    start = find_start(grid)

    queue = deque([start])
    visited = {}
    min_score = sys.maxsize

    best_path_tiles = {}

    while len(queue) > 0:
        curr = queue.popleft()
        curr.visited.append((curr.x, curr.y))

        if grid[curr.y][curr.x] == "E":
            best_path_tiles.setdefault(curr.score, set()).update(curr.visited)
            min_score = min(min_score, curr.score)
            continue
        visited[(curr.x, curr.y)] = curr.score

        for n in get_next(curr, grid):
            if (n.x, n.y) not in visited or visited[(n.x, n.y)] >= n.score:
                queue.append(n)

    print(f"Part1: {min_score}")
    lowest_key = min(best_path_tiles.keys())
    print(f"Part2: {len(best_path_tiles[lowest_key])}")


def get_next(curr, grid):
    next_list = []

    forward = (curr.x + direction_map[curr.direction][0], curr.y + direction_map[curr.direction][1])
    if grid[forward[1]][forward[0]] != "#":
        next_list.append(Node(forward[0], forward[1], curr.direction, curr.score + 1, curr.visited.copy()))

    clockwise_dir = rotation_clockwise_map[curr.direction]
    clockwise = (curr.x + direction_map[clockwise_dir][0], curr.y + direction_map[clockwise_dir][1])
    if grid[clockwise[1]][clockwise[0]] != "#":
        next_list.append(Node(clockwise[0], clockwise[1], clockwise_dir, curr.score + 1001, curr.visited.copy()))

    counter_clockwise_dir = rotation_counter_clockwise_map[curr.direction]
    counter_clockwise = (
        curr.x + direction_map[counter_clockwise_dir][0], curr.y + direction_map[counter_clockwise_dir][1])
    if grid[counter_clockwise[1]][counter_clockwise[0]] != "#":
        next_list.append(Node(counter_clockwise[0], counter_clockwise[1], counter_clockwise_dir, curr.score + 1001,
                              curr.visited.copy()))

    return next_list


def find_start(grid):
    for row_i, line in enumerate(grid):
        for col_i, c in enumerate(line):
            if c == "S":
                return Node(col_i, row_i, ">", 0, [])


solve()
