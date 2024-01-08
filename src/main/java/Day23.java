import Common.Coord;
import Common.Direction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day23 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        SimpleProfiler profiler = new SimpleProfiler().start();
        solvePart2();
        profiler.stop();
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

            // Before this I did brute forcing this with -Xss20m for a couple of min,
            // inspected max and got the right answer :D
            // Here is a better approach but still room for improv as pretty slow ~35sec
            buildGraph(startingCoord, map);

            Path startingPath = MEMO.get(new Coord(1, 0)).get(0);
            Set<Path> seen = new HashSet<>();
            seen.add(startingPath);
            System.out.println(dfsIntersectedMap(startingPath, seen));
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


    private static int dfsIntersectedMap(Path current, Set<Path> visitedPaths) {
        if (current.toIntersection.tile.coord.y() == 140 && current.toIntersection.tile.coord.x() == 139) {
            return visitedPaths.stream().map(p -> p.path.size() + 1).reduce(Integer::sum).orElse(0);
        }
        int maxLength = 0;
        List<Path> possiblePaths = Optional.ofNullable(MEMO.get(current.toIntersection.tile.coord))
                .orElse(Collections.emptyList());
        if (possiblePaths.isEmpty()) {
            return 0;
        }
        for (Path next : possiblePaths) {
            if (visitedPaths.stream().anyMatch(visited -> visited.fromIntersection.tile.coord.equals(next.toIntersection.tile.coord))) {
                continue;
            }
            visitedPaths.add(next);
            int maxNext = dfsIntersectedMap(next, visitedPaths);
            maxLength = Math.max(maxNext, maxLength);
            visitedPaths.remove(next);
        }

        return maxLength;
    }

    private static void print(Set<Path> paths, Tile[][] map) {
        for (Tile[] row : map) {
            for (Tile t : row) {
                if (paths.stream().anyMatch(p -> p.toIntersection.tile.equals(t) ||
                        p.fromIntersection.tile.equals(t) ||
                        p.path.stream().anyMatch(tP -> tP.tile.equals(t)))) {
                    System.out.print("0");
                } else {
                    System.out.print(t.type);
                }
            }
            System.out.println();
        }
    }

    private static final Map<Coord, List<Path>> MEMO = new HashMap<>();

    private static class Path {
        private final TilePair fromIntersection;
        private TilePair toIntersection;

        public Path(TilePair fromIntersection) {
            this.fromIntersection = fromIntersection;
        }

        TilePair getLast() {
            if (!path.isEmpty()) {
                return path.get(path.size() - 1);
            }
            return null;
        }

        void removeLast() {
            path.remove(path.size() - 1);
        }

        private List<TilePair> path = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path path1 = (Path) o;
            return Objects.equals(fromIntersection, path1.fromIntersection) && Objects.equals(toIntersection, path1.toIntersection) && Objects.equals(path, path1.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromIntersection, toIntersection, path);
        }
    }


    private static void buildGraph(TilePair start, Tile[][] map) {
        Stack<Path> queue = new Stack<>();
        Set<Coord> visited = new HashSet<>();

        Path startBuilder = new Path(start);
        startBuilder.path.add(getPossiblePaths(start, map).get(0));

        queue.add(startBuilder);

        while (!queue.isEmpty()) {
            Path current = queue.pop();

            List<TilePair> path = current.path;
            List<TilePair> possiblePaths = getPossiblePaths(current.getLast(), map);
            while (possiblePaths.size() == 1) {
                TilePair next = possiblePaths.get(0);
                path.add(next);
                possiblePaths = getPossiblePaths(next, map);
            }
            if (possiblePaths.size() > 1) {
                current.path = new ArrayList<>(path);
                current.toIntersection = path.get(path.size() - 1);
                current.removeLast();
                for (TilePair pathItem : current.path) {
                    visited.add(pathItem.tile.coord);
                }

                addToIntersectionMap(current);

                Path reversedCurrent = new Path(current.toIntersection);
                reversedCurrent.toIntersection = current.fromIntersection;
                reversedCurrent.path = new ArrayList<>(current.path);
                Collections.reverse(reversedCurrent.path);
                addToIntersectionMap(reversedCurrent);

                for (TilePair next : possiblePaths) {
                    if (!visited.contains(next.tile.coord)) {
                        Path newPathForNext = new Path(current.toIntersection);
                        newPathForNext.path.add(next);
                        queue.add(newPathForNext);
                    }
                }
            }
            if (possiblePaths.isEmpty()) {
                TilePair last = path.get(path.size() - 1);
                if (last.tile.coord.y() == map.length - 1 && last.tile.coord.x() == map[0].length - 2) {
                    current.path = new ArrayList<>(path);
                    current.toIntersection = path.get(path.size() - 1);
                    current.removeLast();
                    MEMO.putIfAbsent(current.fromIntersection.tile.coord, new ArrayList<>());
                    MEMO.get(current.fromIntersection.tile.coord).add(current);
                }
            }
        }
    }

    private static void addToIntersectionMap(Path current) {
        if (MEMO.get(current.fromIntersection.tile.coord) != null) {
            if (MEMO.get(current.fromIntersection.tile.coord).stream()
                    .noneMatch(p -> p.toIntersection.tile.coord.equals(current.toIntersection.tile.coord))) {
                MEMO.get(current.fromIntersection.tile.coord).add(current);
            }
        } else {
            MEMO.putIfAbsent(current.fromIntersection.tile.coord, new ArrayList<>());
            MEMO.get(current.fromIntersection.tile.coord).add(current);
        }
    }

    private static List<TilePair> getAllPossiblePaths(TilePair tile, Tile[][] map) {
        return getInDirections(List.of(Direction.DOWN, Direction.LEFT, Direction.UP, Direction.RIGHT), tile, map);
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
