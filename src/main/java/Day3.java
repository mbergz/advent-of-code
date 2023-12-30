import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day2.class.getResource("day3.txt").toURI()))) {
            List<String> lines = stream.toList();
            int resultPart1 = 0;

            for (int i = 0; i < lines.size(); i++) {
                String prevLine = i > 0 ? lines.get(i - 1) : null;
                String nextLine = i < lines.size() - 1 ? lines.get(i + 1) : null;
                String currentLine = lines.get(i);

                int sumOfValidNumbers = createNumberWithIndex(currentLine).stream()
                        .filter(numberWithIndex -> isValid(numberWithIndex, currentLine, prevLine, nextLine))
                        .map(NumberWithIndex::number)
                        .reduce(Integer::sum)
                        .orElse(0);
                resultPart1 += sumOfValidNumbers;
            }
            System.out.println(resultPart1);
        }
    }

    private static boolean isValid(NumberWithIndex partNbr, String currentLine, String prevLine, String nextLine) {
        if (verifyCurrentLine(partNbr, currentLine)) {
            return true;
        }
        if (verifyLine(partNbr, prevLine)) {
            return true;
        }
        return verifyLine(partNbr, nextLine);
    }

    private static boolean verifyCurrentLine(NumberWithIndex partNbr, String currentLine) {
        if (partNbr.startIndex > 0) {
            if (currentLine.charAt(partNbr.startIndex - 1) != '.' &&
                    !Character.isDigit(currentLine.charAt(partNbr.startIndex - 1))) {
                return true;
            }
        }
        if (partNbr.endIndex <= currentLine.length() - 1) {
            if (currentLine.charAt(partNbr.endIndex + 1) != '.' &&
                    !Character.isDigit(currentLine.charAt(partNbr.endIndex + 1))) {
                return true;
            }
        }
        return false;
    }

    private static boolean verifyLine(NumberWithIndex partNbr, String line) {
        if (line != null) {
            int startIndex = partNbr.startIndex == 0 ? 0 : partNbr.startIndex - 1;
            int endIndex = partNbr.endIndex == line.length() - 1 ? line.length() - 1 : partNbr.endIndex + 1;
            for (int i = startIndex; i <= endIndex; i++) {
                if (line.charAt(i) != '.' && !Character.isDigit(line.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private record NumberWithIndex(int number, int startIndex, int endIndex) {
        public boolean coversIndex(int index) {
            return startIndex <= index && index <= endIndex;
        }
    }


    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day2.class.getResource("day3.txt").toURI()))) {
            List<String> lines = stream.toList();
            int resultPart2 = 0;

            for (int i = 0; i < lines.size(); i++) {
                List<NumberWithIndex> prev = i > 0 ? createNumberWithIndex(lines.get(i - 1)) : Collections.emptyList();
                List<NumberWithIndex> current = createNumberWithIndex(lines.get(i));
                List<NumberWithIndex> next = i < lines.size() - 1 ? createNumberWithIndex(lines.get(i + 1)) : Collections.emptyList();

                String currentLine = lines.get(i);
                for (int j = 0; j < currentLine.length(); j++) {
                    if (currentLine.charAt(j) == '*') {
                        resultPart2 += getGearRatio(j, prev, current, next);
                    }
                }
            }
            System.out.println(resultPart2);
        }
    }

    private static int getGearRatio(int starIndex,
                                    List<NumberWithIndex> prev,
                                    List<NumberWithIndex> current,
                                    List<NumberWithIndex> next) {
        List<NumberWithIndex> allFound = Stream.of(
                        findNumberAdjacentToStar(prev, starIndex),
                        findNumberAdjacentToStar(current, starIndex),
                        findNumberAdjacentToStar(next, starIndex))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .toList();
        if (allFound.size() == 2) {
            return allFound.stream().map(NumberWithIndex::number).reduce((a, b) -> a * b).orElse(0);
        }
        return 0;
    }

    private static List<NumberWithIndex> findNumberAdjacentToStar(List<NumberWithIndex> numbers, int starIndex) {
        return numbers.stream()
                .filter(nbr -> nbr.coversIndex(starIndex - 1) ||
                        nbr.coversIndex(starIndex) ||
                        nbr.coversIndex(starIndex + 1))
                .collect(Collectors.toList());
    }

    private static List<NumberWithIndex> createNumberWithIndex(String line) {
        StringBuilder sb = new StringBuilder();
        List<NumberWithIndex> list = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                sb.append(line.charAt(i));
                if (i == line.length() - 1) {
                    list.add(new NumberWithIndex(Integer.parseInt(sb.toString()), i - sb.length(), i - 1));
                    sb.setLength(0);
                }
            } else if (!sb.isEmpty()) {
                list.add(new NumberWithIndex(Integer.parseInt(sb.toString()), i - sb.length(), i - 1));
                sb.setLength(0);
            }
        }
        return list;
    }

}
