import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day1 {
    public static void main(String[] args) throws Exception {
        solve();
    }

    private static final Map<String, Integer> NUMBERS_MAP = Map.of(
            "one", 1,
            "two", 2,
            "three", 3,
            "four", 4,
            "five", 5,
            "six", 6,
            "seven", 7,
            "eight", 8,
            "nine", 9
    );

    private static void solve() throws Exception {
        SimpleProfiler profiler = new SimpleProfiler().start();
        try (Stream<String> lines = Files.lines(Paths.get(Day1.class.getResource("day1.txt").toURI()))) {
            int resultPart1 = lines.map(Day1::extractSumPart1).reduce(0, Integer::sum);
            System.out.println(resultPart1);
        }
        profiler.stop();

        profiler.start();
        try (Stream<String> lines = Files.lines(Paths.get(Day1.class.getResource("day1.txt").toURI()))) {
            int resultPart2 = lines.map(Day1::extractSumPart2).reduce(0, Integer::sum);
            System.out.println(resultPart2);
        }
        profiler.stop();
    }

    private static int extractSumPart1(String line) {
        int leftIndex = 0;
        int rightIndex = line.length() - 1;
        String leftNbr = "";
        String rightNbr = "";
        while (leftIndex <= rightIndex) {
            if (Character.isDigit(line.charAt(leftIndex))) {
                leftNbr = String.valueOf(line.charAt(leftIndex));
            } else {
                leftIndex++;
            }
            if (Character.isDigit(line.charAt(rightIndex))) {
                rightNbr = String.valueOf(line.charAt(rightIndex));
            } else {
                rightIndex--;
            }
            if (!leftNbr.isEmpty() && !rightNbr.isEmpty()) {
                break;
            }
        }
        return Integer.parseInt(leftNbr + rightNbr);
    }

    private static int extractSumPart2(String line) {
        List<String> identifiedNbrs = extractAllWords(line);
        String firstNbr = identifiedNbrs.get(0);
        String firstNbrModified = NUMBERS_MAP.containsKey(firstNbr) ? String.valueOf(NUMBERS_MAP.get(firstNbr)) : firstNbr;
        String lastNbr = identifiedNbrs.get(identifiedNbrs.size() - 1);
        String lastNbrModified = NUMBERS_MAP.containsKey(lastNbr) ? String.valueOf(NUMBERS_MAP.get(lastNbr)) : lastNbr;

        return Integer.parseInt(firstNbrModified + lastNbrModified);
    }

    private static List<String> extractAllWords(String line) {
        List<String> wordsInOrder = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                wordsInOrder.add(String.valueOf(line.charAt(i)));
            } else {
                for (int j = line.length(); j > i; j--) {
                    String subStr = line.substring(i, j);
                    if (NUMBERS_MAP.containsKey(subStr)) {
                        wordsInOrder.add(subStr);
                    }
                }
            }
        }
        return wordsInOrder;
    }
}
