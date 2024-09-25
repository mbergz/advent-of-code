import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    // 12 RED
    // 13 GREEN
    // 14 BLUE
    private static void solvePart1() throws Exception {
        try (Stream<String> lines = Files.lines(Paths.get(Day2.class.getResource("day2.txt").toURI()))) {
            List<String> linesList = lines.toList();
            int resultPart1 = 0;
            int gameNumber = 0;

            for (String line : linesList) {
                gameNumber++;
                String stripped = line.substring(line.indexOf(":") + 1).trim();
                String[] sets = stripped.split(";");
                if (Arrays.stream(sets).allMatch(Day2::checkSet)) {
                    resultPart1 += gameNumber;
                }
            }
            System.out.println(resultPart1);
        }
    }

    private static boolean checkSet(String set) {
        String[] colorStrings = set.split(",");
        return Arrays.stream(colorStrings).allMatch(Day2::isValid);
    }

    private static boolean isValid(String colorString) {
        int nbr = Integer.parseInt(colorString.trim().split(" ")[0]);
        String color = colorString.trim().split(" ")[1];
        return switch (color.toLowerCase()) {
            case "red" -> nbr <= 12;
            case "green" -> nbr <= 13;
            case "blue" -> nbr <= 14;
            default -> true;
        };
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> lines = Files.lines(Paths.get(Day2.class.getResource("day2.txt").toURI()))) {
            List<String> linesList = lines.toList();
            int resultPart2 = 0;
            for (String line : linesList) {
                resultPart2 += getSumOfPowerMinSetCubes(line);
            }
            System.out.println(resultPart2);
        }
    }

    private static int getSumOfPowerMinSetCubes(String line) {
        String stripped = line.substring(line.indexOf(":") + 1).trim();
        String[] sets = stripped.split(";");

        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;
        for (String set : sets) {
            String[] pairs = set.split(",");
            for (String pair : pairs) {
                int nbr = Integer.parseInt(pair.trim().split(" ")[0]);
                String color = pair.trim().split(" ")[1];
                switch (color) {
                    case "red" -> maxRed = Math.max(nbr, maxRed);
                    case "green" -> maxGreen = Math.max(nbr, maxGreen);
                    case "blue" -> maxBlue = Math.max(nbr, maxBlue);
                }
            }
        }
        return maxRed * maxGreen * maxBlue;
    }
}
