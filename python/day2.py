file1 = open('./../src/main/resources/day2.txt', 'r')
lines = file1.readlines()


def part1():
    result = 0
    for index, line in enumerate(lines):
        games = line.split(":")[1].split(";")
        if all(valid_game(x) for x in games):
            result += index + 1
    print(result)


def valid_game(game):
    cube_sets = game.split(",")
    return all(valid_set(x) for x in cube_sets)


def valid_set(cube_set):
    number, color = cube_set.strip().split()
    mapping = {'red': 12, 'green': 13, 'blue': 14}
    return int(number) <= mapping[color]


def part2():
    print(sum(get_power_of_cube_set(line) for line in lines))


def get_power_of_cube_set(line):
    max_values = {'red': 0, 'green': 0, 'blue': 0}
    for game in line.split(":")[1].split(";"):
        for cube_set in game.split(","):
            number, color = cube_set.strip().split()
            max_values[color] = max(int(number), max_values[color])
    return max_values['red'] * max_values['green'] * max_values['blue']


part1()
part2()
