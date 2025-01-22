import os
import sys
import time


class PuzzleRunner:
    def __init__(self):
        file_name = os.path.basename(sys.argv[0])
        base_name = os.path.splitext(file_name)[0]
        input_file_name = base_name + ".txt"
        with open(input_file_name, 'r') as input_file:
            self.lines = input_file.readlines()

    def run(self, part1, part2=None):
        self.__time_function(part1, "Part1")
        if part2:
            print()
            self.__time_function(part2, "Part2")

    def __time_function(self, func, func_name):
        start = time.time()
        print(f"{func_name}: ")
        func(self.lines)
        time_taken_ms = (time.time() - start) * 1000
        print(f"Execution time: {time_taken_ms:.4f} ms")
