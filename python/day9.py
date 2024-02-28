file = open('./../src/main/resources/day9.txt', 'r')
lines = file.readlines()


def build_rows(nbr_line):
    result = [nbr_line]
    row_index = 0
    while True:
        prev_row = result[row_index]
        new_row = []
        for i in range(1, len(prev_row)):
            num1 = prev_row[i - 1]
            num2 = prev_row[i]
            new_row.append(num2 - num1)
        result.append(new_row)
        if all(nbr == 0 for nbr in new_row):
            break
        row_index += 1
    return result


def part1():
    result = 0
    for line in lines:
        rows = build_rows([int(num) for num in line.split()])
        rows.reverse()
        for index, row in enumerate(rows):
            last_elem_prev_row = rows[index - 1][-1] if index != 0 else 0
            last_elem_curr_row = row[-1]
            row.append(last_elem_curr_row + last_elem_prev_row)
        top_row = rows[-1]
        result += top_row[-1]
    return result


print(part1())
