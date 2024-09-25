import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Day24 {

    private static final long LOW_LIMIT = 200000000000000L;
    private static final long MAX_LIMIT = 400000000000000L;

    public static void main(String[] args) throws Exception {
        SimpleProfiler profiler = new SimpleProfiler().start();
        solvePart1();
        profiler.stop();
    }

    /**
     * y=kx+m
     * k = deltaY/deltaX
     * m=y-kx
     * x=(m2-m1)/(k1-k2)
     */
    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day24.class.getResource("day24.txt").toURI()))) {
            List<LinearHailstonePath> paths = stream.map(line -> {
                long x1 = Long.parseLong(line.split("@")[0].strip().split(",")[0].strip());
                long y1 = Long.parseLong(line.split("@")[0].strip().split(",")[1].strip());
                long deltaX = Long.parseLong(line.split("@")[1].strip().split(",")[0].strip());
                long deltaY = Long.parseLong(line.split("@")[1].strip().split(",")[1].strip());
                return new LinearHailstonePath(x1, y1, deltaX, deltaY);
            }).toList();
            System.out.println(countValidIntersections(paths));
        }
    }

    private static int countValidIntersections(List<LinearHailstonePath> paths) {
        int valid = 0;

        for (int i = 0; i < paths.size(); i++) {
            for (int j = i + 1; j < paths.size(); j++) {
                LinearHailstonePath current = paths.get(i);
                LinearHailstonePath other = paths.get(j);

                if (current.getK() == other.getK()) {
                    continue;
                }
                long x = (long) ((other.getM() - current.getM()) / (current.getK() - other.getK()));
                if (isPast(current.vx, current.px, x) || isPast(other.vx, other.px, x)) {
                    continue;
                }
                long y = (long) (current.getK() * x + current.getM());
                if (isPast(current.vy, current.py, y) || isPast(other.vy, other.py, y)) {
                    continue;
                }
                if ((LOW_LIMIT <= x && x <= MAX_LIMIT) && (LOW_LIMIT <= y && y <= MAX_LIMIT)) {
                    valid++;
                }
            }
        }
        return valid;
    }

    private static boolean isPast(long vNbr, long pNbr, long intersectionPointValue) {
        if (vNbr > 0) {
            return pNbr > intersectionPointValue;
        } else {
            return pNbr < intersectionPointValue;
        }
    }

    private record LinearHailstonePath(long px, long py, long vx, long vy) {
        double getK() {
            return (double) vy / vx;
        }

        double getM() {
            return py - getK() * px;
        }
    }

}
