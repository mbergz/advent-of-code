input_file = open('day12.txt', 'r')
lines = input_file.readlines()


def part1():
    grid = []
    all_plants = []
    for row_index, line in enumerate(lines):
        grid.append(line.strip())
        for col_index, val in enumerate(line.strip()):
            all_plants.append((col_index, row_index, val))

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
            right = (curr[0] + 1, curr[1], curr[2])
            if is_matching_plant(grid, right):
                if right not in visited and right not in queue:
                    queue.append(right)
                perimeter_score -= 1

            down = (curr[0], curr[1] + 1, curr[2])
            if is_matching_plant(grid, down):
                if down not in visited and down not in queue:
                    queue.append(down)
                perimeter_score -= 1

            left = (curr[0] - 1, curr[1], curr[2])
            if is_matching_plant(grid, left):
                if left not in visited and left not in queue:
                    queue.append(left)
                perimeter_score -= 1

            up = (curr[0], curr[1] - 1, curr[2])
            if is_matching_plant(grid, up):
                if up not in visited and up not in queue:
                    queue.append(up)
                perimeter_score -= 1

            perimeter += perimeter_score

        score += area * perimeter

    print(score)


def is_matching_plant(grid, plant):
    if 0 <= plant[0] < len(grid[0]) and 0 <= plant[1] < len(grid):
        return plant[2] == grid[plant[1]][plant[0]]
    return False


def part2():
    print("score")


part1()
part2()
