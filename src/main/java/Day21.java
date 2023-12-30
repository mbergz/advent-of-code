import Common.Coord;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day21 {
    public static void main(String[] args) throws Exception {
        solvePart1();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day21.class.getResource("day21.txt").toURI()))) {
            List<String> lines = stream.toList();
            Coord startCoord = findStartCoord(lines);
            GardenPlots[][] grid = createGrid(lines);

            traverseBFS(new GardenPlots(startCoord), grid);

            System.out.println(Arrays.stream(grid)
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(GardenPlots::isVisited)
                    .count());
        }
    }

    private static void traverseBFS(GardenPlots start, GardenPlots[][] grid) {
        LinkedList<GardenPlots> queue = new LinkedList<>();
        queue.add(start);

        int level = 0;
        while (!queue.isEmpty() && level < 64) {
            int queueSize = queue.size();
            while (queueSize-- != 0) {
                GardenPlots currentPlot = queue.poll();
                currentPlot.setAsNotVisited();
                List<GardenPlots> nextSteps = getNextSteps(currentPlot, grid);
                for (GardenPlots next : nextSteps) {
                    next.setVisited();
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

        public GardenPlots(Coord coord) {
            this.coord = coord;
        }

        public void setVisited() {
            this.visited = true;
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
    }

}
