from collections import defaultdict

from runner import PuzzleRunner


class Gate:
    def __init__(self, in1: str, in2: str, out: str, operation):
        self.in1 = in1
        self.in2 = in2
        self.out = out
        self.operation = operation
        self.done = False

    def step(self, curr_wire_values):
        val_in_1 = curr_wire_values[self.in1]
        val_in_2 = curr_wire_values[self.in2]
        if val_in_1 != -1 and val_in_2 != -1:
            curr_wire_values[self.out] = self.operation(val_in_1, val_in_2)
            self.done = True


class AndGate(Gate):
    def __init__(self, in1: str, in2: str, out: str):
        super().__init__(in1, in2, out, lambda a, b: a & b)


class OrGate(Gate):
    def __init__(self, in1: str, in2: str, out: str):
        super().__init__(in1, in2, out, lambda a, b: a | b)


class XorGate(Gate):
    def __init__(self, in1: str, in2: str, out: str):
        super().__init__(in1, in2, out, lambda a, b: a ^ b)


def part1(lines):
    curr_wire_values = defaultdict(lambda: -1)  # 1 true, 0 false, -1 no value
    gates = []

    gate_classes = {"XOR": XorGate, "OR": OrGate, "AND": AndGate}
    wires_section = True
    for line in lines:
        if line == "\n":
            wires_section = False
            continue
        if wires_section:
            key, value = line.strip().split(": ")
            curr_wire_values[key] = int(value)
        else:
            inputs, output = line.strip().split("->")
            in1, gate_type, in2 = inputs.split()
            gates.append(gate_classes[gate_type](in1, in2, output.strip()))

    while not all(gate.done for gate in gates):
        for g in gates:
            g.step(curr_wire_values)

    z_keys = [k for k in curr_wire_values.keys() if k.startswith("z")]
    sorted_keys = sorted(z_keys, key=lambda x: int(x[1:]), reverse=True)
    res_bin_str = "".join(str(curr_wire_values[sk]) for sk in sorted_keys)

    print(int(res_bin_str, 2))


PuzzleRunner().run(part1)
