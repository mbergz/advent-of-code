import Common.Coord;
import Common.Direction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day23 {

    public static void main(String[] args) throws Exception {
        SimpleProfiler profiler = new SimpleProfiler().start();
        solvePart1();
        profiler.stop();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day23.class.getResource("day23.txt").toURI()))) {
            List<String> lines = stream.toList();
            Tile[][] map = createMap(lines);

            TilePair startingCoord = new TilePair(map[0][1], Direction.UP);
            int max = dfs(startingCoord, new HashSet<>(), map);
            System.out.println(max);
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day23.class.getResource("day23.txt").toURI()))) {
            List<String> lines = stream.toList();
            Tile[][] map = createMapPart2(lines);

            TilePair startingCoord = new TilePair(map[0][1], Direction.UP);

            // Brute forcing this with -Xss20m for a couple of min and
            // inspecting current max gave me right answer :D , TODO - optimize
            dfsBruteForcePart2(startingCoord, new HashSet<>(), map);
        }
    }

    private static int dfs(TilePair tilePair, Set<Coord> visitedSoFar, Tile[][] map) {
        if (tilePair.tile.coord.y() == map.length - 1 && tilePair.tile.coord.x() == map[0].length - 2) {
            return visitedSoFar.size();
        }

        int maxLength = 0;
        List<TilePair> possiblePaths = getPossiblePaths(tilePair, map);
        for (TilePair next : possiblePaths) {
            if (visitedSoFar.contains(next.tile.coord)) {
                continue;
            }
            visitedSoFar.add(next.tile.coord);
            int maxNext = dfs(next, visitedSoFar, map);
            maxLength = Math.max(maxNext, maxLength);
            visitedSoFar.remove(next.tile.coord);
        }
        return maxLength;
    }

    private static int max = 0;

    private static int dfsBruteForcePart2(TilePair tilePair, Set<Coord> visitedSoFar, Tile[][] map) {
        if (tilePair.tile.coord.y() == map.length - 1 && tilePair.tile.coord.x() == map[0].length - 2) {
            max = Math.max(visitedSoFar.size(), max);
            return visitedSoFar.size();
        }

        int maxLength = 0;
        List<TilePair> possiblePaths = getPossiblePaths(tilePair, map);
        if (possiblePaths.stream().allMatch(pP -> visitedSoFar.contains(pP.tile.coord))) {
            return 0;
        }
        for (TilePair next : possiblePaths) {
            if (visitedSoFar.contains(next.tile.coord)) {
                continue;
            }
            visitedSoFar.add(next.tile.coord);
            int maxNext = dfsBruteForcePart2(next, visitedSoFar, map);
            maxLength = Math.max(maxNext, maxLength);
            visitedSoFar.remove(next.tile.coord);
        }
        return maxLength;
    }

    private static List<TilePair> getPossiblePaths(TilePair tile, Tile[][] map) {
        if (tile.tile.type == '^') {
            return Collections.singletonList(getNextUp(tile, map).get());
        } else if (tile.tile.type == '>') {
            return Collections.singletonList(getNextRight(tile, map).get());
        } else if (tile.tile.type == 'v') {
            return Collections.singletonList(getNextDown(tile, map).get());
        } else if (tile.tile.type == '<') {
            return Collections.singletonList(getNextLeft(tile, map).get());
        }

        return switch (tile.from) {
            case RIGHT -> getInDirections(List.of(Direction.DOWN, Direction.LEFT, Direction.UP), tile, map);
            case DOWN -> getInDirections(List.of(Direction.LEFT, Direction.UP, Direction.RIGHT), tile, map);
            case LEFT -> getInDirections(List.of(Direction.UP, Direction.RIGHT, Direction.DOWN), tile, map);
            case UP -> getInDirections(List.of(Direction.RIGHT, Direction.DOWN, Direction.LEFT), tile, map);
        };
    }

    private static List<TilePair> getInDirections(List<Direction> newDirs, TilePair fromTile, Tile[][] map) {
        List<TilePair> res = new ArrayList<>();
        for (Direction direction : newDirs) {
            switch (direction) {
                case RIGHT -> getNextRight(fromTile, map).ifPresent(res::add);
                case DOWN -> getNextDown(fromTile, map).ifPresent(res::add);
                case LEFT -> getNextLeft(fromTile, map).ifPresent(res::add);
                case UP -> getNextUp(fromTile, map).ifPresent(res::add);
            }
        }
        return res;
    }

    private static Optional<TilePair> getNextLeft(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x() - 1, fromTile.tile.coord.y());
        if (isValid(newCoord, map) && map[newCoord.y()][newCoord.x()].type != '>') {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.RIGHT));
        }
        return Optional.empty();
    }

    private static Optional<TilePair> getNextUp(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x(), fromTile.tile.coord.y() - 1);
        if (isValid(newCoord, map) && map[newCoord.y()][newCoord.x()].type != 'v') {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.DOWN));
        }
        return Optional.empty();
    }

    private static Optional<TilePair> getNextRight(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x() + 1, fromTile.tile.coord.y());
        if (isValid(newCoord, map) && map[newCoord.y()][newCoord.x()].type != '<') {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.LEFT));
        }
        return Optional.empty();
    }

    private static Optional<TilePair> getNextDown(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x(), fromTile.tile.coord.y() + 1);
        if (isValid(newCoord, map) && map[newCoord.y()][newCoord.x()].type != '^') {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.UP));
        }
        return Optional.empty();
    }


    private static boolean isValid(Coord coord, Tile[][] map) {
        boolean withinBounds = coord.x() >= 0 && coord.x() < map[0].length &&
                coord.y() >= 0 && coord.y() < map.length;

        return withinBounds && map[coord.y()][coord.x()].type != '#';
    }


    private static Tile[][] createMap(List<String> lines) {
        Tile[][] res = new Tile[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                res[y][x] = new Tile(new Coord(x, y), lines.get(y).charAt(x));
            }
        }
        return res;
    }

    private static Tile[][] createMapPart2(List<String> lines) {
        Tile[][] res = new Tile[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                char cChar = lines.get(y).charAt(x);
                if (cChar != '.' && cChar != '#') {
                    cChar = '.';
                }
                res[y][x] = new Tile(new Coord(x, y), cChar);
            }
        }
        return res;
    }

    private record TilePair(Tile tile, Direction from) {
    }

    private record Tile(Coord coord, char type) {
    }

}
