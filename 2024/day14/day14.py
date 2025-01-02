input_file = open('day14.txt', 'r')
lines = input_file.readlines()

WIDTH = 101
HEIGHT = 103


def part1():
    robots = {}
    for i, line in enumerate(lines):
        pos = extract_pos(line)
        velo = extract_velo(line)
        robots[i] = (pos, velo)
    # print_map(robots)
    for _ in range(100):
        for k in robots:
            update_pos(k, robots)
    # print_map(robots)
    print(count_quadrants(robots))


def part2():
    print("score")


# 234906525 too high
def count_quadrants(robots):
    first = 0
    second = 0
    third = 0
    fourth = 0
    for k in robots:
        robot = robots[k]
        if 0 <= robot[0][0] < int(WIDTH / 2) and 0 <= robot[0][1] < int(HEIGHT / 2):
            first += 1
            continue
        if int(WIDTH / 2) < robot[0][0] < WIDTH and 0 <= robot[0][1] < int(HEIGHT / 2):
            second += 1
            continue
        if int(WIDTH / 2) < robot[0][0] < WIDTH and int(HEIGHT / 2) < robot[0][1] < HEIGHT:
            third += 1
            continue
        if 0 <= robot[0][0] < int(WIDTH / 2) and int(HEIGHT / 2) < robot[0][1] < HEIGHT:
            fourth += 1
            continue
    return first * second * third * fourth


def update_pos(key, robots):
    curr = robots[key]
    curr_pos = curr[0]
    new_x = (curr_pos[0] + curr[1][0]) % WIDTH
    new_y = (curr_pos[1] + curr[1][1]) % HEIGHT
    robots[key] = ((new_x, new_y), curr[1])


def extract_velo(line):
    velo_line = line.strip().split(" ")[1]
    x = int(velo_line.split("=")[1].split(",")[0])
    y = int(velo_line.split("=")[1].split(",")[1])
    return x, y


def extract_pos(line):
    pos_line = line.strip().split(" ")[0]
    x = int(pos_line.split("=")[1].split(",")[0])
    y = int(pos_line.split("=")[1].split(",")[1])
    return x, y


def print_map(robots):
    for r in range(0, HEIGHT):
        for c in range(0, WIDTH):
            found_nbr = 0
            for k in robots:
                if robots[k][0] == (c, r):
                    found_nbr += 1
            if found_nbr == 0:
                print(". ", end="")
            else:
                print(str(found_nbr) + " ", end="")
        print("")
    print("")


part1()
part2()
