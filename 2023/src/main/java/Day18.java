import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day18 {
    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day18.class.getResource("day18.txt").toURI()))) {
            Function<String, Character> charExtractor = line -> line.split(" ")[0].charAt(0);
            Function<String, Integer> numberExtractor = line -> Integer.parseInt(line.split(" ")[1]);
            runAlgorithm(stream, charExtractor, numberExtractor);
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day18.class.getResource("day18.txt").toURI()))) {
            Function<String, Character> charExtractor = line -> {
                String[] split = line.split(" ");
                String color = split[2].substring(1, split[2].length() - 1);
                return convertToDirectionChar(Character.getNumericValue(color.charAt(color.length() - 1)));
            };
            Function<String, Integer> numberExtractor = line -> {
                String[] split = line.split(" ");
                String color = split[2].substring(1, split[2].length() - 1);
                return Integer.valueOf(color.substring(1, color.length() - 1), 16);
            };
            runAlgorithm(stream, charExtractor, numberExtractor);
        }
    }

    private static void runAlgorithm(Stream<String> stream,
                                     Function<String, Character> directionExtractor,
                                     Function<String, Integer> numberExtractor) {
        int b = 0;
        List<DoubleCoord> coords = new ArrayList<>();
        DoubleCoord currentCoord = new DoubleCoord(0, 0);
        for (String line : stream.toList()) {
            char direction = directionExtractor.apply(line);
            int number = numberExtractor.apply(line);
            b += number;
            currentCoord = switch (direction) {
                case 'L' -> currentCoord.goLeft(number);
                case 'U' -> currentCoord.goUp(number);
                case 'R' -> currentCoord.goRight(number);
                case 'D' -> currentCoord.goDown(number);
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            coords.add(currentCoord);
        }

        long A = shoelaceArea(coords);
        long i = runPicksTheorem(b, A);
        // Pick's Therom and Shoelace formula together  i + b = A + (b/2) +1
        System.out.println(i + b);
    }


    private static char convertToDirectionChar(Integer nbr) {
        return switch (nbr) {
            case 0 -> 'R';
            case 1 -> 'D';
            case 2 -> 'L';
            case 3 -> 'U';
            default -> throw new IllegalStateException("Unexpected value: " + nbr);
        };
    }

    /**
     * https://en.wikipedia.org/wiki/Pick%27s_theorem
     * <p>
     * A = i + (b/2) - 1
     * ....
     * i = A - (b/2) + 1
     */
    private static long runPicksTheorem(int b, long A) {
        return A - (b / 2) + 1;
    }

    /**
     * https://en.wikipedia.org/wiki/Shoelace_formula
     * <p>
     * A = 1/2 * sum(i->n)[xi*((yi+1)-(yi-1))]
     */
    private static long shoelaceArea(List<DoubleCoord> coords) {
        int n = coords.size();
        double a = 0.0;

        a += coords.get(0).x() * (coords.get(1).y() - coords.get(n - 1).y());
        for (int i = 1; i < n - 1; i++) {
            a += coords.get(i).x() * (coords.get(i + 1).y() - coords.get(i - 1).y());
        }
        return (long) (Math.abs(a + coords.get(n - 1).x() * (coords.get(0).y() - coords.get(n - 2).y())) / 2.0);
    }

    private record DoubleCoord(double x, double y) {
        public static DoubleCoord of(double x, double y) {
            return new DoubleCoord(x, y);
        }

        public DoubleCoord goRight(double nbr) {
            return new DoubleCoord(this.x + nbr, this.y);
        }

        public DoubleCoord goUp(double nbr) {
            return new DoubleCoord(this.x, this.y - nbr);
        }

        public DoubleCoord goLeft(double nbr) {
            return new DoubleCoord(this.x - nbr, this.y);
        }

        public DoubleCoord goDown(double nbr) {
            return new DoubleCoord(this.x, this.y + nbr);
        }
    }
}
