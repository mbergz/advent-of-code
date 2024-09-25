import Common.Coord;
import Common.Direction;
import Common.FromCoord;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day17 {

    public static void main(String[] args) throws Exception {
        solve();
    }

    private static void solve() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day17.class.getResource("day17.txt").toURI()))) {
            int[][] grid = createGrid(stream.toList());
            System.out.println(runDijkstra(grid, false));
            System.out.println(runDijkstra(grid, true));
        }
    }

    private static int[][] createGrid(List<String> lines) {
        int[][] tiles = new int[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            tiles[y] = lines.get(y).chars().map(Character::getNumericValue).toArray();
        }
        return tiles;
    }

    private static int runDijkstra(int[][] grid, boolean part2) {
        PriorityQueue<WeightedNode> queue = new PriorityQueue<>();
        queue.add(new WeightedNode(new Node(new FromCoord(new Coord(1, 0), Direction.LEFT), 1), grid[0][1]));
        queue.add(new WeightedNode(new Node(new FromCoord(new Coord(0, 1), Direction.UP), 1), grid[1][0]));

        Set<Node> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            WeightedNode current = queue.poll();
            Node currentNode = current.node();

            if (visited.contains(currentNode)) {
                continue;
            }
            visited.add(currentNode);

            if (current.node().coord().coord().equals(new Coord(grid[0].length - 1, grid.length - 1)) &&
                    (!part2 || currentNode.consecutive() >= 4)) {
                return current.heatLoss();
            }

            queue.addAll(adjacentNodes(current, grid, part2));
        }
        return -1;
    }

    private static List<WeightedNode> createdAdjacentNodes(int[][] grid,
                                                           WeightedNode current,
                                                           Direction currentDirection,
                                                           List<Direction> toDirections,
                                                           boolean ultraCrucible) {
        List<WeightedNode> nodes = new ArrayList<>();
        if (!ultraCrucible || current.node().consecutive() >= 4) {
            for (Direction toDir : toDirections) {
                switch (toDir) {
                    case DOWN -> newNode(grid, current, Day17::createNextDown).ifPresent(nodes::add);
                    case LEFT -> newNode(grid, current, Day17::createNextLeft).ifPresent(nodes::add);
                    case UP -> newNode(grid, current, Day17::createNextUp).ifPresent(nodes::add);
                    case RIGHT -> newNode(grid, current, Day17::createNextRight).ifPresent(nodes::add);
                }
            }
        }
        switch (currentDirection) {
            case RIGHT -> newNode(grid, current, Day17::createNextRight, ultraCrucible ? 10 : 3).ifPresent(nodes::add);
            case DOWN -> newNode(grid, current, Day17::createNextDown, ultraCrucible ? 10 : 3).ifPresent(nodes::add);
            case LEFT -> newNode(grid, current, Day17::createNextLeft, ultraCrucible ? 10 : 3).ifPresent(nodes::add);
            case UP -> newNode(grid, current, Day17::createNextUp, ultraCrucible ? 10 : 3).ifPresent(nodes::add);
        }
        return nodes;
    }

    private static Optional<WeightedNode> newNode(int[][] grid,
                                                  WeightedNode current,
                                                  Function<Node, FromCoord> newCoordFn) {
        FromCoord newCoord = newCoordFn.apply(current.node());
        if ((newCoord.coord().y() >= 0 && newCoord.coord().y() < grid.length) &&
                (newCoord.coord().x() >= 0 && newCoord.coord().x() < grid[0].length)) {
            return Optional.of(new WeightedNode(new Node(newCoord, 1),
                    current.heatLoss() + grid[newCoord.coord().y()][newCoord.coord().x()]));
        }
        return Optional.empty();
    }

    private static Optional<WeightedNode> newNode(int[][] grid,
                                                  WeightedNode current,
                                                  Function<Node, FromCoord> newCoordFn,
                                                  int consecutiveLimit) {
        FromCoord newCoord = newCoordFn.apply(current.node());
        if ((newCoord.coord().y() >= 0 && newCoord.coord().y() < grid.length) &&
                (newCoord.coord().x() >= 0 && newCoord.coord().x() < grid[0].length)) {
            if (current.node().consecutive() < consecutiveLimit) {
                return Optional.of(new WeightedNode(new Node(newCoord, current.node().consecutive() + 1),
                        current.heatLoss() + grid[newCoord.coord().y()][newCoord.coord().x()]));
            }
        }
        return Optional.empty();
    }


    private static List<WeightedNode> adjacentNodes(WeightedNode current, int[][] grid, boolean ultraCrucible) {
        return switch (current.node().coord().from()) {
            case LEFT -> createdAdjacentNodes(grid, current, Direction.RIGHT, List.of(Direction.UP, Direction.DOWN), ultraCrucible);
            case UP -> createdAdjacentNodes(grid, current, Direction.DOWN, List.of(Direction.RIGHT, Direction.LEFT), ultraCrucible);
            case RIGHT -> createdAdjacentNodes(grid, current, Direction.LEFT, List.of(Direction.DOWN, Direction.UP), ultraCrucible);
            case DOWN -> createdAdjacentNodes(grid, current, Direction.UP, List.of(Direction.LEFT, Direction.RIGHT), ultraCrucible);
        };
    }

    private static FromCoord createNextRight(Node current) {
        return FromCoord.of(Coord.of(current.coord().coord().x() + 1, current.coord().coord().y()), Direction.LEFT);
    }

    private static FromCoord createNextUp(Node current) {
        return FromCoord.of(Coord.of(current.coord().coord().x(), current.coord().coord().y() - 1), Direction.DOWN);
    }

    private static FromCoord createNextDown(Node current) {
        return FromCoord.of(Coord.of(current.coord().coord().x(), current.coord().coord().y() + 1), Direction.UP);
    }

    private static FromCoord createNextLeft(Node current) {
        return FromCoord.of(Coord.of(current.coord().coord().x() - 1, current.coord().coord().y()), Direction.RIGHT);
    }

    private record WeightedNode(Node node, int heatLoss) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode o) {
            return Integer.compare(this.heatLoss, o.heatLoss());
        }
    }

    private record Node(FromCoord coord, int consecutive) {
    }

}
