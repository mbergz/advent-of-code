import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {

    public static void main(String[] args) throws Exception {
        solve();
    }

    private static void solve() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day9.class.getResource("day9.txt").toURI()))) {
            List<String> lines = stream.toList();

            int resultPart1 = 0;
            int resultPart2 = 0;
            for (String line : lines) {
                LinkedList<Integer> base = Arrays.stream(line.split(" "))
                        .map(Integer::parseInt)
                        .collect(Collectors.toCollection(LinkedList::new));

                List<LinkedList<Integer>> rows = buildRowsForOneLine(base);
                fillInValues(rows);
                LinkedList<Integer> topRow = rows.get(rows.size() - 1);
                resultPart1 += topRow.getLast();
                resultPart2 += topRow.getFirst();
            }
            System.out.println(resultPart1);
            System.out.println(resultPart2);
        }
    }

    private static void fillInValues(List<LinkedList<Integer>> rows) {
        Collections.reverse(rows);
        for (int i = 0; i < rows.size(); i++) {
            fillInNextValue(rows, i);
            fillInFirstValue(rows, i);
        }
    }

    private static void fillInNextValue(List<LinkedList<Integer>> rows, int i) {
        int lastPrev = i != 0 ? rows.get(i - 1).getLast() : 0;
        int lastCurr = rows.get(i).getLast();
        int newLastCurr = lastCurr + lastPrev;
        rows.get(i).addLast(newLastCurr);
    }

    private static void fillInFirstValue(List<LinkedList<Integer>> rows, int i) {
        int firstPrev = i != 0 ? rows.get(i - 1).get(0) : 0;
        int firstCurr = rows.get(i).getFirst();
        int newLastCurr = firstCurr - firstPrev;
        rows.get(i).addFirst(newLastCurr);
    }

    private static List<LinkedList<Integer>> buildRowsForOneLine(LinkedList<Integer> base) {
        List<LinkedList<Integer>> rows = new ArrayList<>();
        rows.add(base);
        int rowIndex = 0;

        while (true) {
            LinkedList<Integer> newRow = new LinkedList<>();
            LinkedList<Integer> prevRow = rows.get(rowIndex);
            for (int i = 1; i < prevRow.size(); i++) {
                int prev = prevRow.get(i - 1);
                int curr = prevRow.get(i);
                newRow.add(curr - prev);
            }
            rows.add(newRow);
            if (newRow.stream().allMatch(nbr -> nbr == 0)) {
                break;
            }
            rowIndex++;
        }
        return rows;
    }

}
