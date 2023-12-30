import Common.CommonMatrix;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day14 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        SimpleProfiler sp = new SimpleProfiler().start();
        solvePart2(); //93742
        sp.stop();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day14.class.getResource("day14.txt").toURI()))) {
            List<String> grid = stream.toList();
            List<String> tiltedColumns = tiltLeft(CommonMatrix.getGridAsColumns(grid));
            System.out.println(calculateScore(tiltedColumns)); //105003
        }
    }


    // cycle 80 is equal cycle 131
    // 80+51x+y=1 000 000 000
    // (1 000 000 000 - 80) % 51 = 29
    // 80+51+29=160
    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day14.class.getResource("day14.txt").toURI()))) {
            List<String> rotatedGrid = stream.toList();
            List<List<String>> cycles = new ArrayList<>();
            boolean foundCycle = false;
            int cycleNbr = Integer.MAX_VALUE;
            for (int i = 0; i < cycleNbr; i++) {
                rotatedGrid = tiltNorth(rotatedGrid);
                rotatedGrid = tiltWest(rotatedGrid);
                rotatedGrid = tilSouth(rotatedGrid);
                rotatedGrid = tiltEast(rotatedGrid);
                if (!foundCycle) {
                    for (int j = 0; j < cycles.size(); j++) {
                        if (cycles.get(j).equals(rotatedGrid)) {
                            cycleNbr = i + ((1000000000 - j) % (i - j)) - 1;
                            foundCycle = true;
                        }
                    }
                    cycles.add(new ArrayList<>(rotatedGrid));
                }
            }
            System.out.println(calculateScore(CommonMatrix.getGridAsColumns(rotatedGrid)));
        }
    }

    private static List<String> tiltEast(List<String> rotatedGrid) {
        rotatedGrid = reverseOrderGrid(rotatedGrid);
        rotatedGrid = tiltLeft(rotatedGrid);
        rotatedGrid = reverseOrderGrid(rotatedGrid);
        return rotatedGrid;
    }

    private static List<String> tilSouth(List<String> rotatedGrid) {
        rotatedGrid = CommonMatrix.getGridAsColumns(rotatedGrid);
        rotatedGrid = reverseOrderGrid(rotatedGrid);
        rotatedGrid = tiltLeft(rotatedGrid);
        rotatedGrid = reverseOrderGrid(rotatedGrid);
        rotatedGrid = CommonMatrix.getGridAsColumns(rotatedGrid);
        return rotatedGrid;
    }

    private static List<String> tiltWest(List<String> rotatedGrid) {
        return tiltNorthOrWest(rotatedGrid);
    }

    private static List<String> tiltNorthOrWest(List<String> rotatedGrid) {
        rotatedGrid = CommonMatrix.getGridAsColumns(rotatedGrid);
        rotatedGrid = tiltLeft(rotatedGrid);
        return rotatedGrid;
    }

    private static List<String> tiltNorth(List<String> rotatedGrid) {
        return tiltNorthOrWest(rotatedGrid);
    }

    private static List<String> reverseOrderGrid(List<String> rotatedGrid) {
        return rotatedGrid.stream()
                .map(line -> new StringBuilder(line).reverse().toString())
                .toList();
    }

    private static List<String> tiltLeft(List<String> rows) {
        List<String> tilted = new ArrayList<>();
        for (String s : rows) {
            char[] chars = s.toCharArray();
            List<Integer> rollRockIndexes = new ArrayList<>();
            for (int i = chars.length - 1; i >= 0; i--) {
                if (s.charAt(i) == '#') {
                    int temp = i;
                    for (int rollIndex : rollRockIndexes) {
                        chars[rollIndex] = '.';
                    }
                    for (int k = 0; k < rollRockIndexes.size(); k++) {
                        chars[++temp] = 'O';
                    }
                    rollRockIndexes = new ArrayList<>();
                } else if (chars[i] == 'O') {
                    rollRockIndexes.add(i);
                }
            }
            if (!rollRockIndexes.isEmpty()) {
                int temp = 0;
                for (int rollIndex : rollRockIndexes) {
                    chars[rollIndex] = '.';
                }
                for (int k = 0; k < rollRockIndexes.size(); k++) {
                    chars[temp++] = 'O';
                }
            }
            String res = new String(chars);
            tilted.add(res);
        }
        return tilted;
    }

    private static int calculateScore(List<String> tiltedColumns) {
        int total = 0;
        for (String s : tiltedColumns) {
            int columnCount = 0;
            int score = s.length();
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'O') {
                    columnCount += score;
                }
                score--;
            }
            total += columnCount;
        }
        return total;
    }

}
