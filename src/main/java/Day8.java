import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 {
    private static final String START_KEY = "AAA";
    private static final String GOAL_KEY = "ZZZ";
    private static final long GREATEST_COMMON_DIVISOR = 307L;

    public static void main(String[] args) throws Exception {
        solve();
    }

    private static void solve() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day8.class.getResource("day8.txt").toURI()))) {
            List<String> lines = stream.toList();
            String leftRightInstructions = lines.get(0);

            Map<String, Pair> network = lines.subList(2, lines.size()).stream()
                    .collect(Collectors.toMap(line -> line.split("=")[0].trim(), Day8::createLeftRightPair));

            System.out.println(calculateNbrOfStepsPart1(leftRightInstructions, network));
            System.out.println(calculateNbrOfStepsPart2(leftRightInstructions, network));
        }
    }

    private static int calculateNbrOfStepsPart1(String leftRightInstructions, Map<String, Pair> network) {
        int nbrOfSteps = 0;
        String currentKey = START_KEY;

        while (true) {
            for (Character instruction : leftRightInstructions.toCharArray()) {
                nbrOfSteps++;
                if (instruction.equals('L')) {
                    currentKey = network.get(currentKey).left();
                } else {
                    currentKey = network.get(currentKey).right();
                }
                if (currentKey.equals(GOAL_KEY)) {
                    return nbrOfSteps;
                }
            }
        }
    }

    private static long calculateNbrOfStepsPart2(String leftRightInstructions, Map<String, Pair> network) {
        List<String> startingKeys = getAllNodesEndingWithA(network);
        List<Long> nbrOfStepsToFirstZ = new ArrayList<>();
        for (String key : startingKeys) {
            nbrOfStepsToFirstZ.add(getStepsToFirstEndingZ(key, leftRightInstructions, network));
        }
        return leastCommonMultipleOfList(nbrOfStepsToFirstZ);
    }

    private static long leastCommonMultipleOfList(List<Long> input) {
        long result = input.get(0);
        for (int i = 1; i < input.size(); i++)
            result = leastCommonMultiple(result, input.get(i));
        return result;
    }

    private static long leastCommonMultiple(long a, long b) {
        return a * (b / GREATEST_COMMON_DIVISOR);
    }

    private static long getStepsToFirstEndingZ(String startingKey,
                                               String leftRightInstructions,
                                               Map<String, Pair> network) {
        long nbrOfSteps = 0;
        String currentKey = startingKey;
        while (true) {
            for (Character instruction : leftRightInstructions.toCharArray()) {
                nbrOfSteps++;
                if (instruction.equals('L')) {
                    currentKey = network.get(currentKey).left();
                } else {
                    currentKey = network.get(currentKey).right();
                }
                if (currentKey.charAt(2) == 'Z') {
                    return nbrOfSteps;
                }
            }
        }
    }

    private static List<String> getAllNodesEndingWithA(Map<String, Pair> network) {
        return network.keySet().stream().filter(key -> key.charAt(2) == 'A').toList();
    }


    private static Pair createLeftRightPair(String line) {
        String[] splitEquals = line.split("=");
        String[] leftRight = splitEquals[1].substring(2, splitEquals[1].length() - 1).split(", ");
        return new Pair(leftRight[0], leftRight[1]);
    }

    private record Pair(String left, String right) {
    }

}
