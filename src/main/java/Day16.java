import Common.Coord;
import Common.Direction;
import Common.FromCoord;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day16 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        SimpleProfiler profiler = new SimpleProfiler().start();
        solvePart2();
        profiler.stop();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day16.class.getResource("day16.txt").toURI()))) {
            List<String> lines = stream.toList();
            char[][] grid = createGrid(lines);
            System.out.println(traverseBFS(grid, new FromCoord(new Coord(0, 0), Direction.LEFT)));
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day16.class.getResource("day16.txt").toURI()))) {
            List<String> lines = stream.toList();
            char[][] grid = createGrid(lines);

            int maxEnergizedTiles = 0;
            for (int y = 0; y < grid.length; y++) {
                maxEnergizedTiles = Math.max(maxEnergizedTiles, traverseBFS(grid, new FromCoord(new Coord(0, y), Direction.LEFT)));
                maxEnergizedTiles = Math.max(maxEnergizedTiles, traverseBFS(grid, new FromCoord(new Coord(grid[0].length, y), Direction.RIGHT)));
            }
            for (int x = 0; x < grid[0].length; x++) {
                maxEnergizedTiles = Math.max(maxEnergizedTiles, traverseBFS(grid, new FromCoord(new Coord(x, 0), Direction.UP)));
                maxEnergizedTiles = Math.max(maxEnergizedTiles, traverseBFS(grid, new FromCoord(new Coord(x, grid.length), Direction.DOWN)));
            }
            System.out.println(maxEnergizedTiles);
        }
    }

    private static char[][] createGrid(List<String> lines) {
        char[][] tiles = new char[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            tiles[y] = lines.get(y).toCharArray();
        }
        return tiles;
    }

    private static int traverseBFS(char[][] grid, FromCoord starting) {
        LinkedList<FromCoord> queue = new LinkedList<>();
        Set<FromCoord> visited = new HashSet<>();
        queue.add(starting);

        while (!queue.isEmpty()) {
            FromCoord item = queue.poll();
            if (visited.contains(item) ||
                    (item.coord().x() >= grid[0].length || item.coord().x() < 0) ||
                    (item.coord().y() >= grid.length || item.coord().y() < 0)) {
                continue;
            }
            visited.add(item);
            char currentChar = grid[item.coord().y()][item.coord().x()];
            if (shouldContinueSamePath(currentChar, item.from())) {
                queue.add(new FromCoord(getNextCoordSameDirection(item.coord(), item.from()), item.from()));
            } else if (currentChar == '\\') {
                handleMirrorLeft(item.from(), queue, item.coord());
            } else if (currentChar == '/') {
                handleMirrorRight(item.from(), queue, item.coord());
            } else {
                handleFlatSideSplitter(item.from(), queue, item.coord());
            }
        }
        return (int) visited.stream().map(FromCoord::coord).distinct().count();
    }

    private static void handleFlatSideSplitter(Direction from, LinkedList<FromCoord> queue, Coord current) {
        switch (from) {
            case LEFT, RIGHT -> {
                queue.add(new FromCoord(new Coord(current.x(), current.y() + 1), Direction.UP));
                queue.add(new FromCoord(new Coord(current.x(), current.y() - 1), Direction.DOWN));
            }
            case UP, DOWN -> {
                queue.add(new FromCoord(new Coord(current.x() + 1, current.y()), Direction.LEFT));
                queue.add(new FromCoord(new Coord(current.x() - 1, current.y()), Direction.RIGHT));
            }
        }
    }

    private static void handleMirrorRight(Direction from, LinkedList<FromCoord> queue, Coord current) {
        switch (from) {
            case LEFT -> queue.add(new FromCoord(new Coord(current.x(), current.y() - 1), Direction.DOWN));
            case UP -> queue.add(new FromCoord(new Coord(current.x() - 1, current.y()), Direction.RIGHT));
            case RIGHT -> queue.add(new FromCoord(new Coord(current.x(), current.y() + 1), Direction.UP));
            case DOWN -> queue.add(new FromCoord(new Coord(current.x() + 1, current.y()), Direction.LEFT));
        }
    }

    private static void handleMirrorLeft(Direction from, LinkedList<FromCoord> queue, Coord current) {
        switch (from) {
            case LEFT -> queue.add(new FromCoord(new Coord(current.x(), current.y() + 1), Direction.UP));
            case UP -> queue.add(new FromCoord(new Coord(current.x() + 1, current.y()), Direction.LEFT));
            case RIGHT -> queue.add(new FromCoord(new Coord(current.x(), current.y() - 1), Direction.DOWN));
            case DOWN -> queue.add(new FromCoord(new Coord(current.x() - 1, current.y()), Direction.RIGHT));
        }
    }

    private static boolean shouldContinueSamePath(char current, Direction from) {
        if (current == '.') {
            return true;
        }
        return switch (from) {
            case UP, DOWN -> current == '|';
            case RIGHT, LEFT -> current == '-';
        };
    }

    private static Coord getNextCoordSameDirection(Coord current, Direction from) {
        return switch (from) {
            case UP -> new Coord(current.x(), current.y() + 1);
            case RIGHT -> new Coord(current.x() - 1, current.y());
            case DOWN -> new Coord(current.x(), current.y() + -1);
            case LEFT -> new Coord(current.x() + 1, current.y());
        };
    }
}
