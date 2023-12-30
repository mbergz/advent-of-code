import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day6 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        SimpleProfiler simpleProfiler = new SimpleProfiler().start();
        solvePart2();
        simpleProfiler.stop();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day6.class.getResource("day6.txt").toURI()))) {
            List<String> lines = stream.toList();
            List<Integer> timeLimits = Arrays.stream(lines.get(0).split(":")[1].trim().split("\\s+")).map(Integer::parseInt).toList();
            List<Integer> records = Arrays.stream(lines.get(1).split(":")[1].trim().split("\\s+")).map(Integer::parseInt).toList();

            List<Integer> nbrOfWaysWinList = new ArrayList<>();
            for (int i = 0; i < timeLimits.size(); i++) {
                long timeLimit = timeLimits.get(i);
                long record = records.get(i);
                nbrOfWaysWinList.add(getNbrOfWaysToWin(timeLimit, record));
            }
            System.out.println(nbrOfWaysWinList.stream().reduce((a, b) -> a * b).orElse(0));
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day6.class.getResource("day6.txt").toURI()))) {
            List<String> lines = stream.toList();
            long timeLimit = Long.parseLong(lines.get(0).split(":")[1].trim().replaceAll("\\s+", ""));
            long record = Long.parseLong(lines.get(1).split(":")[1].trim().replaceAll("\\s+", ""));
            System.out.println(getNbrOfWaysToWin(timeLimit, record));
        }
    }

    /**
     * x^2 + px + q = 0
     * x = -(p/2) ± sqrt( (p/2)^2 -q )
     * <p>
     * x = button press, p = -timelimit, q = record
     * <p>
     * x1 = first time we pass win rate
     * x2 = second time we pass but now on way down
     */
    private static int getNbrOfWaysToWin(double timeLimit, double record) {
        double sqrt = Math.sqrt(Math.pow(timeLimit / 2, 2) - record);
        double x1 = timeLimit / 2 + sqrt;
        double x2 = timeLimit / 2 - sqrt;
        return ((int) x1) - ((int) x2);
    }

}
