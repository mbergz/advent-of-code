import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }


    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day2.class.getResource("day4.txt").toURI()))) {
            List<String> lines = stream.toList();
            int resultPart1 = 0;

            for (String line : lines) {
                int lineScore = 0;
                String cut = line.split(":")[1];
                Set<Integer> winningNbrs = Arrays.stream(cut.split("\\|")[0].trim().split("\\s+"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());
                List<Integer> gameNumbers = Arrays.stream(cut.split("\\|")[1].trim().split("\\s+"))
                        .map(Integer::parseInt)
                        .toList();
                for (Integer nbr : gameNumbers) {
                    if (winningNbrs.contains(nbr)) {
                        lineScore = lineScore == 0 ? 1 : lineScore * 2;
                    }
                }
                resultPart1 += lineScore;
            }
            System.out.println(resultPart1);
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day2.class.getResource("day4.txt").toURI()))) {
            List<ScratchCard> scratchCards = stream.map(line -> new ScratchCard(line, 1)).toList();
            int resultPart2 = 0;

            for (int i = 0; i < scratchCards.size(); i++) {
                ScratchCard scratchCard = scratchCards.get(i);
                int score = scratchCardScorePart2(scratchCard.getLine());
                for (int j = 0; j < scratchCard.getCopies(); j++) {
                    int temp = i;
                    for (int k = 0; k < score; k++) {
                        temp++;
                        if (k > scratchCards.size() - i) {
                            break;
                        }
                        scratchCards.get(temp).incrementCopy();
                    }
                }
                resultPart2 += scratchCard.getCopies();
            }
            System.out.println(resultPart2);
        }
    }

    private static int scratchCardScorePart2(String line) {
        String cut = line.split(":")[1];
        Set<Integer> winningNbrs = Arrays.stream(cut.split("\\|")[0].trim().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        List<Integer> gameNumbers = Arrays.stream(cut.split("\\|")[1].trim().split("\\s+"))
                .map(Integer::parseInt)
                .toList();
        return (int) gameNumbers.stream().filter(winningNbrs::contains).count();
    }


    private static class ScratchCard {
        private final String line;
        private int copies;

        public ScratchCard(String line, int copies) {
            this.line = line;
            this.copies = copies;
        }

        public String getLine() {
            return line;
        }

        public int getCopies() {
            return copies;
        }


        public void incrementCopy() {
            this.copies += 1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScratchCard that = (ScratchCard) o;
            return copies == that.copies && Objects.equals(line, that.line);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, copies);
        }
    }

}
