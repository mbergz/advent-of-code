import Common.Coord;
import Common.Direction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 {

    public static void main(String[] args) throws Exception {
        solvePart1();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day23.class.getResource("day23.txt").toURI()))) {
            List<String> lines = stream.toList();
            Tile[][] map = createMap(lines);

            TilePair startingCoord = new TilePair(map[0][1], Direction.UP);
            //iterativeSearchForPath(startingCoord, map);
            Map<TilePair, Integer> split = new HashMap<>();
            int res = search(startingCoord, 0, map, split);

            int sMax = 0;
            for (int i = 0; i < 1000; i++) {
                int s1 = searchSinglePath(startingCoord, 0, map);
                sMax = Math.max(sMax, s1);
            }
            int t = 2;
            // Now look at map
        }
    }

    private static int searchSinglePath(TilePair currentTile, int currLength, Tile[][] map) {
        Set<TilePair> visited = new HashSet<>();
        List<TilePair> possiblePaths = getPossiblePaths(currentTile, map);
        while (!possiblePaths.isEmpty()) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, possiblePaths.size());
            currentTile = possiblePaths.get(randomNum);
            if (visited.contains(currentTile)) {
                possiblePaths = getPossiblePaths(currentTile, map);
                continue;
            }
            visited.add(currentTile);
            currLength++;
            possiblePaths = getPossiblePaths(currentTile, map);
        }

        if (currentTile.tile.coord.y() == map.length - 1 && currentTile.tile.coord.x() == map[0].length - 2) {
            printPathTaken(map, visited);
            return currLength;
        }

        return -1;
    }

    private static void printPathTaken(Tile[][] map, Set<TilePair> visited) {
        Set<Tile> visitedTile = visited.stream().map(TilePair::tile).collect(Collectors.toSet());
        for (int y = 0; y<map.length; y++) {
            for (int x = 0; x<map[0].length; x++) {
                Tile t = map[y][x];
                if (visitedTile.contains(t)) {
                    System.out.print("0");
                } else {
                    System.out.print(t.type);
                }
            }
            System.out.println();
        }
    }

    private static int search(TilePair currentTile, int currLength, Tile[][] map, Map<TilePair, Integer> split) {

        List<TilePair> possiblePaths = getPossiblePaths(currentTile, map);
        while (!possiblePaths.isEmpty()) {
            for (TilePair tilePair : possiblePaths.subList(1, possiblePaths.size())) {
                split.put(tilePair, currLength + 1);
            }
            currentTile = possiblePaths.get(0);
            currLength++;
            possiblePaths = getPossiblePaths(currentTile, map);
        }

        if (currentTile.tile.coord.y() == map.length - 1 && currentTile.tile.coord.x() == map[0].length - 2) {
            return currLength;
        }

        return -1;
    }

    private static int iterativeSearchForPath(TilePair startTile, Tile[][] map) {
        int count = 0;

        Map<Coord, Integer> distanceMap = new HashMap<>();
        Stack<TilePair> stack = new Stack<>();
        stack.push(startTile);

        while (!stack.isEmpty()) {
            TilePair currentTile = stack.pop();

            if (currentTile.tile.coord.y() == map.length - 1 && currentTile.tile.coord.x() == map[0].length - 2) {
                count++;
                continue;
            }

            count++;
            List<TilePair> possiblePaths = getPossiblePaths(currentTile, map);

            for (TilePair next : possiblePaths) {
                stack.push(next);
            }
        }

        return count;
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
        if (isValid(newCoord, map)) {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.RIGHT));
        }
        return Optional.empty();
    }

    private static Optional<TilePair> getNextUp(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x(), fromTile.tile.coord.y() - 1);
        if (isValid(newCoord, map)) {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.DOWN));
        }
        return Optional.empty();
    }

    private static Optional<TilePair> getNextRight(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x() + 1, fromTile.tile.coord.y());
        if (isValid(newCoord, map)) {
            return Optional.of(new TilePair(map[newCoord.y()][newCoord.x()], Direction.LEFT));
        }
        return Optional.empty();
    }

    private static Optional<TilePair> getNextDown(TilePair fromTile, Tile[][] map) {
        Coord newCoord = new Coord(fromTile.tile.coord.x(), fromTile.tile.coord.y() + 1);
        if (isValid(newCoord, map)) {
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

    private record TilePair(Tile tile, Direction from) {
    }

    private record Tile(Coord coord, char type) {
    }


}
