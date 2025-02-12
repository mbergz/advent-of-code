import re
from collections import deque

from runner import PuzzleRunner

directions = {
    (0, 1): "v",
    (1, 0): ">",
    (-1, 0): "<",
    (0, -1): "^"
}

# num_keypad = [
#     "789",
#     "456",
#     "123",
#     " 0A"
# ]

coord_num_keypad = {
    (1, 3): "0",
    (2, 3): "A",
    (0, 2): "1",
    (1, 2): "2",
    (2, 2): "3",
    (0, 1): "4",
    (1, 1): "5",
    (2, 1): "6",
    (0, 0): "7",
    (1, 0): "8",
    (2, 0): "9",
}

num_keypad_coord = {
    "0": (1, 3),
    "A": (2, 3),
    "1": (0, 2),
    "2": (1, 2),
    "3": (2, 2),
    "4": (0, 1),
    "5": (1, 1),
    "6": (2, 1),
    "7": (0, 0),
    "8": (1, 0),
    "9": (2, 0),
}

# dir_keypad = [
#     " ^A",
#     "<v>",
# ]

dir_keypad_coord = {
    "^": (1, 0),
    "A": (2, 0),
    "<": (0, 1),
    "v": (1, 1),
    ">": (2, 1),
}

coord_dir_keypad = {
    (1, 0): "^",
    (2, 0): "A",
    (0, 1): "<",
    (1, 1): "v",
    (2, 1): ">",
}


def part1(lines):
    res = 0
    for code in lines:
        code_paths = find_paths_for_code(code.strip(), num_keypad_coord, coord_num_keypad)

        all_second_level_paths = []
        for cp in code_paths:
            all_second_level_paths.extend(find_paths_for_code(cp, dir_keypad_coord, coord_dir_keypad))

        min_length_second_level = min(map(len, all_second_level_paths))
        all_second_level_paths = [sub for sub in all_second_level_paths if len(sub) == min_length_second_level]

        all_third_level_paths = []
        for slp in all_second_level_paths:
            all_third_level_paths.extend(find_paths_for_code(slp, dir_keypad_coord, coord_dir_keypad))

        min_length_third_level = min(map(len, all_third_level_paths))
        all_third_level_paths = [sub for sub in all_third_level_paths if len(sub) == min_length_third_level]

        n_match = re.search(r"\d+", code)
        code_nbr = int((n_match.group().lstrip("0")))
        len_shorted = len(all_third_level_paths[0])
        res += (len_shorted * code_nbr)
    print(res)


# Pair moves like >> and << for faster access next level deep
# Prefer bottom row instead of ^. < is furthest away from A
all_dir_keypad = {
    ("<", "A"): ">>^A",
    ("v", "A"): "^>A",
    ("^", "A"): ">A",
    (">", "A"): "^A",
    ("A", "A"): "A",

    ("v", "^"): "^A",
    ("<", "^"): ">^A",
    (">", "^"): "<^A",
    ("A", "^"): "<A",
    ("^", "^"): "A",

    ("A", ">"): "vA",
    ("v", ">"): ">A",
    ("^", ">"): "v>A",
    ("<", ">"): ">>A",
    (">", ">"): "A",

    ("A", "v"): "<vA",
    (">", "v"): "<A",
    ("^", "v"): "vA",
    ("<", "v"): ">A",
    ("v", "v"): "A",

    ("A", "<"): "v<<A",
    (">", "<"): "<<A",
    ("^", "<"): "v<A",
    ("v", "<"): "<A",
    ("<", "<"): "A",

}

path_cache = {}


def expand_path_25(curr):
    path = curr
    for _ in range(10):
        path = expand_sub(path)

    res = 0
    prev = "A"
    for p in path:
        res += path_cache[(prev, p)]
        prev = p
    return res


def populate_cache_15_levels():
    for e in all_dir_keypad:
        path = e[1]
        for i in range(15):
            if i == 0:
                path = expand_sub(path, e[0])
            else:
                path = expand_sub(path, "A")
        path_cache[e] = len(path)


def expand_sub(curr, prev="A"):
    res = ""
    for char in curr:
        res += (all_dir_keypad[(prev, char)])
        prev = char
    return res


def part2(lines):
    res1 = 0
    populate_cache_15_levels()
    for code in lines:
        code_paths = find_paths_for_code(code.strip(), num_keypad_coord, coord_num_keypad)

        length = float("inf")
        for i, cp in enumerate(code_paths):
            length = min(length, expand_path_25(cp))

        n_match = re.search(r"\d+", code)
        code_nbr = int((n_match.group().lstrip("0")))
        res1 += (length * code_nbr)
    print(res1)


def find_paths_for_code(code, keypad_coord_map, coord_keypad_map):
    paths = [""]
    start = keypad_coord_map["A"]

    for target_nbr in code:
        # Run level order bfs
        goal = keypad_coord_map[target_nbr]
        visited = set(start)
        queue = deque([(start, "", 0)])  # pos, str path, depth

        sub_paths = []

        goal_depth = -1
        while len(queue) > 0:
            for _ in range(len(queue)):
                curr, path, depth = queue.popleft()
                visited.add(curr)

                if curr == goal:
                    sub_paths.append(path + "A")
                    if goal_depth == -1:
                        goal_depth = depth

                if goal_depth != -1 and depth >= goal_depth:
                    continue

                for d in directions:
                    next_coord = (curr[0] + d[0], curr[1] + d[1])
                    if next_coord in coord_keypad_map and next_coord not in visited:
                        queue.append((next_coord, path + directions[d], depth + 1))

        paths = [p + sp for p in paths for sp in sub_paths]

        start = goal

    return paths


PuzzleRunner().run(part1, part2)
