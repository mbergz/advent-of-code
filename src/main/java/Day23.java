import Common.Coord;
import Common.Direction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 {

    public static void main(String[] args) throws Exception {
        solvePart1();
    }

    private static int getMaxFromLists(List<State> stateList) {
        int max = 0;
        for (State s : stateList) {
            if (s.done()) {
                max = Math.max(max, s.currLength());
            }
        }
        return max;
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day23.class.getResource("day23.txt").toURI()))) {
            List<String> lines = stream.toList();
            Tile[][] map = createMap(lines);

            TilePair startingCoord = new TilePair(map[0][1], Direction.UP);

            State startingState = new State(startingCoord, 0, new HashSet<>(), false);
            List<State> res = search(startingState, map);
            int max = getMaxFromLists(res);

            while (!res.stream().allMatch(State::done)) {
                List<State> newStates = new ArrayList<>();
                List<State> notDone = res.stream().filter(state -> !state.done()).toList();
                for (State notDoneState : notDone) {
                    newStates.addAll(search(notDoneState, map));
                }
                max = Math.max(getMaxFromLists(newStates), max);
                res = newStates;
            }

            System.out.println(max);
        }
    }

    private record State(TilePair tilePair, int currLength, Set<Coord> visited, boolean done) {

    }

    private static List<State> search(State currentState, Tile[][] map) {
        int currLength = currentState.currLength();
        Set<Coord> visited = currentState.visited();
        TilePair currentTile = currentState.tilePair();

        List<State> result = new ArrayList<>();

        List<TilePair> possiblePaths = getPossiblePaths(currentTile, map);
        while (!possiblePaths.isEmpty()) {
            currentTile = possiblePaths.get(0);
            for (TilePair tilePair : possiblePaths.subList(1, possiblePaths.size())) {
                result.add(new State(tilePair, currLength + 1, new HashSet<>(visited), false));
            }

            if (visited.contains(currentTile.tile.coord)) {
                possiblePaths = getPossiblePaths(currentTile, map);
                continue;
            }
            visited.add(currentTile.tile.coord);
            currLength++;
            possiblePaths = getPossiblePaths(currentTile, map);
        }

        if (currentTile.tile.coord.y() == map.length - 1 && currentTile.tile.coord.x() == map[0].length - 2) {
            result.add(new State(currentTile, currLength, new HashSet<>(visited), true));
        }

        return result;
    }

    private static void printPathTaken(Tile[][] map, Set<TilePair> visited) {
        Set<Tile> visitedTile = visited.stream().map(TilePair::tile).collect(Collectors.toSet());
        for (Tile[] tiles : map) {
            for (int x = 0; x < map[0].length; x++) {
                Tile t = tiles[x];
                if (visitedTile.contains(t)) {
                    System.out.print("0");
                } else {
                    System.out.print(t.type);
                }
            }
            System.out.println();
        }
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

    private record TilePair(Tile tile, Direction from) {
    }

    private record Tile(Coord coord, char type) {
    }


}
