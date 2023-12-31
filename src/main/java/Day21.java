import Common.Coord;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day21 {
    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day21.class.getResource("day21.txt").toURI()))) {
            List<String> lines = stream.toList();
            Coord startCoord = findStartCoord(lines);
            GardenPlots[][] grid = createGrid(lines);

            traverseBFS(new GardenPlots(startCoord), grid, 64);

            System.out.println(Arrays.stream(grid)
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(GardenPlots::isVisited)
                    .count());
        }
    }

    private static void printGrid(GardenPlots[][] grid) {
        for (GardenPlots[] gardenPlots : grid) {
            for (GardenPlots gardenPlot : gardenPlots) {
                if (gardenPlot == null) {
                    System.out.print("#");
                } else {
                    if (!gardenPlot.isVisited()) {
                        System.out.print(".");
                    } else {
                        System.out.print("1");
                        /*
                        if (gardenPlot.level == 65) {
                            System.out.print(".");
                        } else if (gardenPlot.level > 65) {
                            System.out.print("X");
                        } else {
                            System.out.print(".");
                        }
                         */
                        //System.out.print(gardenPlot.level);
                        //System.out.print("0");
                    }
                }
            }
            System.out.println();
        }
    }


    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day21.class.getResource("day21.txt").toURI()))) {
            List<String> lines = stream.toList();

            // TEST
            Coord startCoord = findStartCoord(lines);
            GardenPlots[][] grid = createGrid(lines);
            traverseBFS(new GardenPlots(startCoord), grid, 65);
            printGrid(grid);
            // END OF TEST

            // Why does this not work for my input?
            // https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
            long totalOdd = getTotalNumber(lines, 133);
            long totalEven = getTotalNumber(lines, 132);
            long outsideDiamondOdd = getNumberOutsideDiamond(lines, 133);
            long outsideDiamondEven = getNumberOutsideDiamond(lines, 132);

            long size = 26501365 / 131;
            long answer = ((size + 1) * (size + 1) * totalOdd) + (size * size * totalEven) -
                    ((size + 1) * outsideDiamondOdd) + (size * outsideDiamondEven);
            System.out.println(answer); //596734624471510 is wrong
        }
    }

    private static long getNumberOutsideDiamond(List<String> input, int steps) {
        Coord startCoord = findStartCoord(input);
        GardenPlots[][] grid = createGrid(input);
        traverseBFS(new GardenPlots(startCoord), grid, steps);
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(GardenPlots::isVisited)
                .filter(plot -> plot.getLevel() > 65)
                .count();
    }

    private static long getTotalNumber(List<String> input, int steps) {
        Coord startCoord = findStartCoord(input);
        GardenPlots[][] grid = createGrid(input);
        traverseBFS(new GardenPlots(startCoord), grid, steps);
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(GardenPlots::isVisited)
                .count();
    }

    private static void traverseBFS(GardenPlots start, GardenPlots[][] grid, int steps) {
        LinkedList<GardenPlots> queue = new LinkedList<>();
        queue.add(start);

        int level = 0;
        while (!queue.isEmpty() && level < steps) {
            int queueSize = queue.size();
            while (queueSize-- != 0) {
                GardenPlots currentPlot = queue.poll();
                currentPlot.setAsNotVisited();
                List<GardenPlots> nextSteps = getNextSteps(currentPlot, grid);
                for (GardenPlots next : nextSteps) {
                    next.setVisited(level + 1);
                    queue.add(next);
                }
            }
            level++;
        }
    }

    private static Coord findStartCoord(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                char character = lines.get(i).charAt(j);
                if (character == 'S') {
                    return new Coord(j, i);
                }
            }
        }
        throw new IllegalStateException("Should not happen");
    }


    private static List<GardenPlots> getNextSteps(GardenPlots currentPlot, GardenPlots[][] grid) {
        List<GardenPlots> next = new ArrayList<>();
        Coord currentCoord = currentPlot.getCoord();
        findInDirection(currentCoord.oneRight(), grid).filter(plot -> !plot.isVisited()).ifPresent(next::add);
        findInDirection(currentCoord.oneDown(), grid).filter(plot -> !plot.isVisited()).ifPresent(next::add);
        findInDirection(currentCoord.oneLeft(), grid).filter(plot -> !plot.isVisited()).ifPresent(next::add);
        findInDirection(currentCoord.oneUp(), grid).filter(plot -> !plot.isVisited()).ifPresent(next::add);
        return next;
    }

    private static Optional<GardenPlots> findInDirection(Coord coord, GardenPlots[][] grid) {
        if ((coord.x() >= 0 && coord.x() < grid[0].length) &&
                (coord.y() >= 0 && coord.y() < grid.length)) {
            return Optional.ofNullable(grid[coord.y()][coord.x()]);
        }
        return Optional.empty();
    }

    private static GardenPlots[][] createGrid(List<String> lines) {
        GardenPlots[][] grid = new GardenPlots[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                char character = lines.get(i).charAt(j);
                if (character == '.' || character == 'S') {
                    grid[i][j] = new GardenPlots(new Coord(j, i));
                } else {
                    grid[i][j] = null;
                }
            }
        }
        return grid;
    }

    private static class GardenPlots {
        private final Coord coord;
        private boolean visited = false;
        private Integer level;

        public GardenPlots(Coord coord) {
            this.coord = coord;
        }

        public void setVisited(int level) {
            this.visited = true;
            if (this.level == null) {
                this.level = level;
            }
        }

        public void setAsNotVisited() {
            this.visited = false;
        }

        public Coord getCoord() {
            return this.coord;
        }

        public boolean isVisited() {
            return this.visited;
        }

        public int getLevel() {
            return this.level;
        }
    }

}
