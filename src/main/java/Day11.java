import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day11 {

    public static void main(String[] args) throws Exception {
        solvePart1(); //9521776
        solvePart2(); //553224415344
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day11.class.getResource("day11.txt").toURI()))) {
            List<String> grid = getExpandedGrid(stream.toList());
            List<Coord> allGalaxies = getAllGalaxies(grid);
            Map<CoordPair, Long> shortestDistanceMap = createShortestPathMap(allGalaxies);
            System.out.println(shortestDistanceMap.values().stream().reduce(Long::sum).orElse(0L));
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day11.class.getResource("day11.txt").toURI()))) {
            List<String> rawGrid = stream.toList();
            List<Integer> emptyRows = getEmptyRowIndexes(rawGrid);
            List<Integer> emptyColumns = getEmptyColumnIndexes(rawGrid);
            List<Coord> allGalaxies = getAllGalaxies(rawGrid, emptyRows, emptyColumns);
            Map<CoordPair, Long> shortestDistanceMap = createShortestPathMap(allGalaxies);
            System.out.println(shortestDistanceMap.values().stream().reduce(Long::sum).orElse(0L));
        }
    }

    private static List<Coord> getAllGalaxies(List<String> grid) {
        List<Coord> allGalaxies = new ArrayList<>();
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(y).length(); x++) {
                if (grid.get(y).toCharArray()[x] == '#') {
                    allGalaxies.add(new Coord(x, y));
                }
            }
        }
        return allGalaxies;
    }

    private static List<Coord> getAllGalaxies(List<String> grid, List<Integer> emptyRows, List<Integer> emptyColumns) {
        List<Coord> allGalaxies = new ArrayList<>();

        int actualY = 0;
        for (int y = 0; y < grid.size(); y++) {
            int actualX = 0;
            for (int x = 0; x < grid.get(y).length(); x++) {
                if (grid.get(y).toCharArray()[x] == '#') {
                    allGalaxies.add(new Coord(actualX, actualY));
                }
                if (emptyColumns.contains(x)) {
                    actualX += 1000000;
                } else {
                    actualX++;
                }
            }
            if (emptyRows.contains(y)) {
                actualY += 1000000;
            } else {
                actualY++;
            }
        }
        return allGalaxies;
    }

    private static List<Integer> getEmptyColumnIndexes(List<String> rawGrid) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < rawGrid.get(0).length(); i++) {
            StringBuilder sb = new StringBuilder();
            for (String s : rawGrid) {
                sb.append(s.toCharArray()[i]);
            }
            if (!sb.toString().contains("#")) {
                result.add(i);
            }
        }
        return result;
    }

    private static List<Integer> getEmptyRowIndexes(List<String> rawGrid) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < rawGrid.size(); i++) {
            if (!rawGrid.get(i).contains("#")) {
                res.add(i);
            }
        }
        return res;
    }

    private static Map<CoordPair, Long> createShortestPathMap(List<Coord> allGalaxies) {
        Map<CoordPair, Long> shortestDistanceMap = new HashMap<>();
        for (Coord coordA : allGalaxies) {
            List<Coord> coordsToCompareAgainst = allGalaxies.stream()
                    .filter(galaxy -> galaxy != coordA && !shortestDistanceMap.containsKey(new CoordPair(coordA, galaxy)))
                    .toList();
            for (Coord coordB : coordsToCompareAgainst) {
                CoordPair newCoordPair = new CoordPair(coordA, coordB);
                shortestDistanceMap.put(newCoordPair, getShortestPath(coordA, coordB));
            }
        }
        return shortestDistanceMap;
    }

    private static long getShortestPath(Coord fromCoord, Coord toCoord) {
        return Math.abs(toCoord.x() - fromCoord.x()) + Math.abs(toCoord.y() - fromCoord.y());
    }

    private static List<String> getExpandedGrid(List<String> origGrid) {
        List<String> updatedGrid = new ArrayList<>();
        // Expand rows
        for (String string : origGrid) {
            if (!string.contains("#")) {
                updatedGrid.add(string);
            }
            updatedGrid.add(string);
        }

        // Expand columns
        List<Integer> indexesToExpand = new ArrayList<>();
        for (int i = 0; i < updatedGrid.get(0).length(); i++) {
            StringBuilder sb = new StringBuilder();
            for (String s : updatedGrid) {
                sb.append(s.toCharArray()[i]);
            }
            if (!sb.toString().contains("#")) {
                indexesToExpand.add(i);
            }
        }
        return updatedGrid.stream().map(row -> {
            StringBuilder sb = new StringBuilder(row);
            int indexIncrementor = 0;
            for (int index : indexesToExpand) {
                sb.insert(index + indexIncrementor, '.');
                indexIncrementor++;
            }
            return sb.toString();
        }).toList();
    }

    private record CoordPair(Coord a, Coord b) {

        @Override
        public int hashCode() {
            return Objects.hash(a, b) + Objects.hash(b, a);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoordPair coordPair = (CoordPair) o;
            return (Objects.equals(a, coordPair.a) && Objects.equals(b, coordPair.b)) ||
                    (Objects.equals(a, coordPair.b) && Objects.equals(b, coordPair.a));
        }

    }

    private record Coord(int x, int y) {
    }
}


