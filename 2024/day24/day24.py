from collections import defaultdict

from runner import PuzzleRunner


class Gate:
    def __init__(self, in1: str, in2: str, out: str, op_name: str, operation):
        self.in1 = in1
        self.in2 = in2
        self.out = out
        self.name = op_name
        self.operation = operation
        self.done = False

    def step(self, curr_wire_values):
        val_in_1 = curr_wire_values[self.in1]
        val_in_2 = curr_wire_values[self.in2]
        if val_in_1 != -1 and val_in_2 != -1:
            curr_wire_values[self.out] = self.operation(val_in_1, val_in_2)
            self.done = True

    def __repr__(self):
        return f"[Gate, op={self.name}, a={self.in1}, b={self.in2}, out={self.out}]"


class AndGate(Gate):
    def __init__(self, in1: str, in2: str, out: str):
        super().__init__(in1, in2, out, "AND", lambda a, b: a & b)


class OrGate(Gate):
    def __init__(self, in1: str, in2: str, out: str):
        super().__init__(in1, in2, out, "OR", lambda a, b: a | b)


class XorGate(Gate):
    def __init__(self, in1: str, in2: str, out: str):
        super().__init__(in1, in2, out, "XOR", lambda a, b: a ^ b)


def part1(lines):
    curr_wire_values, gates = parse_input(lines)

    while not all(gate.done for gate in gates):
        for g in gates:
            g.step(curr_wire_values)

    z_keys = [k for k in curr_wire_values.keys() if k.startswith("z")]
    sorted_keys = sorted(z_keys, key=lambda x: int(x[1:]), reverse=True)
    res_bin_str = "".join(str(curr_wire_values[sk]) for sk in sorted_keys)

    print(int(res_bin_str, 2))


def part2(lines):
    """
    Half adder + 7 full adders for 8 bit.. half + 44 full. y00 - y44 meaning we have 45 digit binary nbr
    Meaning, verif one half adder present for lowest bit, then it must be full adders all the way to most significant bit
    If error somewhere not making up a full adder, swap with other faulty ones.
    https://www.101computing.net/binary-additions-using-logic-gates/
    """
    curr_wire_values, gates = parse_input(lines)

    res_sum, carry = get_half_adder_gates("x00", "y00", gates)
    if not res_sum or not carry or res_sum.out != "z00":
        raise Exception("INVALID")

    invalid_wires = []

    i = 1
    while i < 45:
        a = f"x{i:02d}"
        b = f"y{i:02d}"
        z_out = f"z{i:02d}"

        # Validate full adder circuit
        fha_sum, fha_carry = get_half_adder_gates(a, b, gates)
        if not fha_sum or not fha_carry:
            raise Exception("First half adder is wrong")

        sha_sum, sha_carry = get_half_adder_gates(carry.out, fha_sum.out, gates)
        if not sha_sum or not sha_carry:
            sha_sum, sha_carry = swap_input_to_half_adder(carry, fha_sum, gates, invalid_wires)

        if sha_sum.out != z_out:
            swap_expected_zout(invalid_wires, gates, sha_sum, z_out)

        or_gate = find_or_gate(fha_carry, sha_carry, gates)
        if not or_gate:
            raise Exception(f"No matching OR gate found for")
        carry = or_gate
        i += 1

    print(",".join(sorted(invalid_wires)))


def parse_input(lines):
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
    return curr_wire_values, gates


def swap_and_check_second_half_adder(target_gate, a, b, gates, found_invalid_wires):
    for g in gates:
        if g != target_gate:
            g.out, target_gate.out = target_gate.out, g.out
            res_ha_sum, res_ha_carry = get_half_adder_gates(a.out, b.out, gates)
            if res_ha_sum and res_ha_carry:
                print(f"Switching out from gate {target_gate} with {g}")
                found_invalid_wires.extend([target_gate.out, g.out])
                return res_ha_sum, res_ha_carry
            g.out, target_gate.out = target_gate.out, g.out
    return None, None


def swap_input_to_half_adder(carry, fha_sum, gates, invalid_wires):
    # Test swap and fix the carry from prev full adder
    swap_sum, swap_carry = swap_and_check_second_half_adder(carry, carry, fha_sum, gates, invalid_wires)
    if swap_sum and swap_carry:
        return swap_sum, swap_carry

    # Test swap and fix sum from first half adder
    swap_sum, swap_carry = swap_and_check_second_half_adder(fha_sum, carry, fha_sum, gates, invalid_wires)
    if swap_sum and swap_carry:
        return swap_sum, swap_carry

    raise Exception("Error")


def swap_expected_zout(found_invalid_wires, gates, sha_sum, z_out):
    for g in gates:
        if g.out == z_out:
            print(f"Switching out from gate {sha_sum} with {g}")
            found_invalid_wires.extend([sha_sum.out, g.out])
            g.out, sha_sum.out = sha_sum.out, g.out
            return
    raise Exception("Should not happen")


def find_or_gate(fha_carry, sha_carry, gates):
    or_gate = next(
        (g for g in gates if
         g.in1 == sha_carry.out and g.in2 == fha_carry.out or g.in1 == fha_carry.out and g.in2 == sha_carry.out),
        None)
    return or_gate


def find_gates(a, b, gates):
    matching_gates = [g for g in gates if g.in1 == a and g.in2 == b or g.in1 == b and g.in2 == a]
    if len(matching_gates) != 2:
        raise Exception("Should not happen")
    return matching_gates


def get_half_adder_gates(a: str, b: str, gates: list[Gate]):
    xor_g = and_g = None

    for g in gates:
        if g.in1 == a and g.in2 == b or g.in1 == b and g.in2 == a:
            if g.name == "XOR":
                xor_g = g
            elif g.name == "AND":
                and_g = g
    # sum, carry gates
    return xor_g, and_g


PuzzleRunner().run(part1, part2)
