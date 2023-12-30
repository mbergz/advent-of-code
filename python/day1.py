file1 = open('./../src/main/resources/day1.txt', 'r')
lines = file1.readlines()


def part1():
    result = []
    for line in lines:
        result.append(extract_numbers(line))
    print(sum(result))


def extract_numbers(line):
    left_index = 0
    right_index = len(line) - 1
    first_nbr = None
    second_nbr = None
    while left_index <= right_index:
        if line[left_index].isdigit():
            first_nbr = line[left_index]
        else:
            left_index += 1
        if line[right_index].isdigit():
            second_nbr = line[right_index]
        else:
            right_index -= 1
        if first_nbr and second_nbr:
            break
    return int(first_nbr + second_nbr)


def replace_number_words_in_line(line):
    word_to_number_map = {
        "one": "o1e",
        "two": "t2o",
        "three": "th3ee",
        "four": "fo4r",
        "five": "fi5e",
        "six": "s6x",
        "seven": "se7en",
        "eight": "ei8ht",
        "nine": "ni9e"
    }
    for word in word_to_number_map:
        line = line.replace(word, word_to_number_map.get(word))

    return line


def part2():
    result = []
    for line in lines:
        line_replaced = replace_number_words_in_line(line)
        result.append(extract_numbers(line_replaced))
    print(sum(result))


part1()
part2()
