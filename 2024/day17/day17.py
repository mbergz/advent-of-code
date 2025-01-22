import math

from runner import PuzzleRunner


class Computer:
    def __init__(self, reg_a: int, reg_b: int, reg_c: int, input: list[int]):
        self.reg_a = reg_a
        self.reg_b = reg_b
        self.reg_c = reg_c
        self.input = input
        self.pointer = 0
        self.output = []
        self.opcode_mapping = {
            0: lambda operand: self.__adv(operand),
            1: lambda operand: self.__bxl(operand),
            2: lambda operand: self.__bst(operand),
            3: lambda operand: self.__jnz(operand),
            4: lambda operand: self.__bxc(operand),
            5: lambda operand: self.__out(operand),
            6: lambda operand: self.__bdv(operand),
            7: lambda operand: self.__cdv(operand),
        }

    def run(self):
        while self.pointer < len(self.input):
            opcode = self.input[self.pointer]
            operand = self.input[self.pointer + 1]
            self.opcode_mapping[opcode](operand)

    def print(self):
        print(",".join(map(str, self.output)))

    def get_output(self):
        return self.output

    def __adv(self, operand):
        denominator = 2 ** self.__get_combo_operand(operand)
        self.reg_a = int(math.trunc(self.reg_a / denominator))
        self.pointer += 2

    def __bxl(self, operand):
        self.reg_b = self.reg_b ^ operand
        self.pointer += 2

    def __bst(self, operand):
        self.reg_b = self.__get_combo_operand(operand) % 8
        self.pointer += 2

    def __jnz(self, operand):
        if self.reg_a == 0:
            self.pointer += 2
            return
        self.pointer = operand

    def __bxc(self, operand):
        self.reg_b = self.reg_b ^ self.reg_c
        self.pointer += 2

    def __out(self, operand):
        combo = self.__get_combo_operand(operand)
        self.output.append(combo % 8)
        self.pointer += 2

    def __bdv(self, operand):
        denominator = 2 ** self.__get_combo_operand(operand)
        self.reg_b = int(math.trunc(self.reg_a / denominator))
        self.pointer += 2

    def __cdv(self, operand):
        denominator = 2 ** self.__get_combo_operand(operand)
        self.reg_c = int(math.trunc(self.reg_a / denominator))
        self.pointer += 2

    def __get_combo_operand(self, operand):
        if 0 <= operand <= 3:
            return operand
        if operand == 4:
            return self.reg_a
        if operand == 5:
            return self.reg_b
        if operand == 6:
            return self.reg_c
        if operand == 7:
            raise Exception("Invalid jump operand 7")


def part1(lines):
    reg_a = int(lines[0].split(":")[1].strip())
    reg_b = int(lines[1].split(":")[1].strip())
    reg_c = int(lines[2].split(":")[1].strip())
    input = list(map(int, lines[4].split(":")[1].strip().split(",")))

    comp = Computer(reg_a, reg_b, reg_c, input)
    comp.run()
    comp.print()


# Thanks /r/adventofcode for some hints
def part2(lines):
    input = list(map(int, lines[4].split(":")[1].strip().split(",")))
    # Find nbr that matches the last nbr in program , then continue left, because of the right shifting in A
    # Start with 0, explore all possible matches, then perform left shift 3 and do same for all 16 nbrs
    results = []
    find_all_possible_solutions(0, input, 15, results)
    print(min(results))


def find_all_possible_solutions(current_a, input_list, find_output_idx, possible_results):
    if find_output_idx < 0:
        return
    length = len(input_list) - find_output_idx

    for i in range(8):
        res = run(current_a, length)
        if res[0] == input_list[find_output_idx]:
            if res == input_list:
                possible_results.append(current_a)
            else:
                find_all_possible_solutions(current_a << 3, input_list, find_output_idx - 1, possible_results)
        current_a += 1


# A % 8, take 3 LSB
# XOR 1 (^1) means flip the least LSB (101 XOR 001 = 100)
# A / 2**B means right shift A B nbr of steps, C = A >> B
# A/ 8 => A >> 3 (A/2**3)
# Each iteration shifts A 3 bits to the right (A *= 8)
def run(nbr, length):
    out_arr = []
    a = nbr
    for _ in range(length):
        b = a % 8
        b = b ^ 1
        c = int(math.trunc(a / (2 ** b)))
        b = b ^ 5
        b = b ^ c
        a = int(math.trunc(a / 8))
        out_arr.append(b % 8)
    return out_arr


PuzzleRunner().run(part1, part2)
