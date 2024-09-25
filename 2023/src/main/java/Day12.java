import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day12 {
    private static final Map<String, Long> MEMORIZED_RECURSIVE_CALLS_MAP = new HashMap<>();

    public static void main(String[] args) throws Exception {
        SimpleProfiler profiler = new SimpleProfiler().start();
        solve();
        profiler.stop();
    }

    private static void solve() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day12.class.getResource("day12.txt").toURI()))) {
            long resulPart1 = 0, resulPart2 = 0;
            for (String line : stream.toList()) {
                String engineParts = line.split(" ")[0];
                List<Integer> groups = Arrays.stream(line.split(" ")[1].split(",")).map(Integer::parseInt).toList();
                resulPart1 += recursiveFindCombinations(groups, engineParts);

                String enginePartsUnfolded = unfoldEngineParts(engineParts);
                List<Integer> unfoldedGroups = unfoldedGroups(groups);
                resulPart2 += recursiveFindCombinations(unfoldedGroups, enginePartsUnfolded);
            }
            System.out.println(resulPart1 + "\n" + resulPart2);
        }
    }

    private static List<Integer> unfoldedGroups(List<Integer> groups) {
        List<Integer> unfoldedGroups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            unfoldedGroups.addAll(groups);
        }
        return unfoldedGroups;
    }

    private static String unfoldEngineParts(String engineParts) {
        StringBuilder enginePartsUnfolded = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            enginePartsUnfolded.append(engineParts);
            if (i < 4) {
                enginePartsUnfolded.append('?');
            }
        }
        return enginePartsUnfolded.toString();
    }


    private static long recursiveFindCombinations(List<Integer> numbers, String part) {
        Long memorized = MEMORIZED_RECURSIVE_CALLS_MAP.get(numbers + part);
        if (memorized != null) {
            return memorized;
        }
        if (part.isEmpty()) {
            return numbers.isEmpty() ? 1 : 0;
        }
        if (numbers.isEmpty()) {
            return part.contains("#") ? 0 : 1;
        }

        long combinations = 0;
        if (part.charAt(0) == '.' || part.charAt(0) == '?') {
            combinations += recursiveFindCombinations(numbers, part.substring(1));
        }
        if (part.charAt(0) == '#' || part.charAt(0) == '?') {
            // Check if substring is at least size of number and contains no dots, only # and ?
            if (numbers.get(0) <= part.length() && !part.substring(0, numbers.get(0)).contains(".")) {
                // If size left is the entire string or we don't have extra # we good
                if (numbers.get(0) == part.length() || part.charAt(numbers.get(0)) != '#') {
                    List<Integer> reducedList = new ArrayList<>(numbers);
                    reducedList.remove(0);
                    // Slice string but take one extra char as it must be gap between broken part groups
                    String newPartString = numbers.get(0) + 1 > part.length() ? "" : part.substring(numbers.get(0) + 1);
                    combinations += recursiveFindCombinations(reducedList, newPartString);
                }
            }
        }

        MEMORIZED_RECURSIVE_CALLS_MAP.put(numbers + part, combinations);
        return combinations;
    }

}
