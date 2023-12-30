import Common.Direction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day10 {
    private static final Map<Character, List<Direction>> VALID_DIRECTIONS_FROM_PIPE = Map.of(
            '-', List.of(Direction.LEFT, Direction.RIGHT),
            '|', List.of(Direction.UP, Direction.DOWN),
            'L', List.of(Direction.UP, Direction.RIGHT),
            'J', List.of(Direction.UP, Direction.LEFT),
            '7', List.of(Direction.LEFT, Direction.DOWN),
            'F', List.of(Direction.RIGHT, Direction.DOWN)
    );

    public static void main(String[] args) throws Exception {
        solve();
    }

    private static void solve() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day10.class.getResource("day10.txt").toURI()))) {
            char[][] rawGrid = stream.map(String::toCharArray).toArray(char[][]::new);
            Coord[][] grid = convertToCoordMatrix(rawGrid);

            Coord sCoord = getStartCoordinate(grid);
            sCoord.setIsPartOfLoop();

            Coord currentCoord = grid[sCoord.getX()][sCoord.getY() + 1];
            currentCoord.setIsPartOfLoop();

            int steps = 1;
            Direction fromDirection = Direction.LEFT;
            while (currentCoord.getPipe() != 'S') {
                Direction nextDirection = getNextDirection(currentCoord, fromDirection);
                fromDirection = updateFromDirection(nextDirection);
                currentCoord = getNextCoordinate(nextDirection, currentCoord, grid);
                currentCoord.setIsPartOfLoop();
                steps++;
            }
            System.out.println("Part1: " + steps / 2);

            long count = solvePart2(grid);
            System.out.println("Part2: " + count);
        }
    }

    private static long solvePart2(Coord[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            boolean search = false;
            for (int x = 0; x < grid[y].length; x++) {
                Coord testA = grid[y][x];
                if (testA.isPartOfLoop()) {
                    if (testA.getPipe() == 'J' || testA.getPipe() == 'L' || testA.getPipe() == '|') {
                        search = !search;
                    }
                } else {
                    if (search) {
                        testA.setIsPartOfEnclosedAreaInLoop();
                    }
                }
            }
        }
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(Coord::isPartOfEnclosedAreaInLoop)
                .count();
    }

    private static Coord[][] convertToCoordMatrix(char[][] grid) {
        Coord[][] res = new Coord[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                res[i][j] = new Coord(grid[i][j], i, j);
            }
        }
        return res;
    }

    private static Coord getStartCoordinate(Coord[][] grid) {
        for (Coord[] coordRow : grid) {
            for (Coord coord : coordRow) {
                if (coord.getPipe() == 'S') {
                    return coord;
                }
            }
        }
        return null;
    }

    private static Coord getNextCoordinate(Direction nextDirection, Coord current, Coord[][] grid) {
        return switch (nextDirection) {
            case UP -> grid[current.getX() - 1][current.getY()];
            case RIGHT -> grid[current.getX()][current.getY() + 1];
            case DOWN -> grid[current.getX() + 1][current.getY()];
            case LEFT -> grid[current.getX()][current.getY() - 1];
        };
    }

    private static Direction getNextDirection(Coord currentCoord, Direction fromDirection) {
        return VALID_DIRECTIONS_FROM_PIPE.get(currentCoord.getPipe()).stream()
                .filter(validDirection -> validDirection != fromDirection)
                .findFirst()
                .orElseThrow();
    }

    private static Direction updateFromDirection(Direction newDirection) {
        return switch (newDirection) {
            case LEFT -> Direction.RIGHT;
            case DOWN -> Direction.UP;
            case RIGHT -> Direction.LEFT;
            case UP -> Direction.DOWN;
        };
    }

    private static class Coord {
        private final char pipe;
        private int x;
        private int y;
        private boolean isPartOfLoop = false;
        private boolean isPartOfEnclosedAreaInLoop = false;

        public Coord(char pipe, int x, int y) {
            this.pipe = pipe;
            this.x = x;
            this.y = y;
        }


        public void setIsPartOfLoop() {
            this.isPartOfLoop = true;
        }

        public void setIsPartOfEnclosedAreaInLoop() {
            this.isPartOfEnclosedAreaInLoop = true;
        }

        public char getPipe() {
            return this.pipe;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean isPartOfLoop() {
            return this.isPartOfLoop;
        }

        public boolean isPartOfEnclosedAreaInLoop() {
            return this.isPartOfEnclosedAreaInLoop;
        }
    }

}
