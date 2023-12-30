import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day15 {
    public static void main(String[] args) throws Exception {
        solvePart1();
        SimpleProfiler sp = new SimpleProfiler().start();
        solvePart2();
        sp.stop();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day15.class.getResource("day15.txt").toURI()))) {
            int res = Arrays.stream(stream.toList().get(0).split(","))
                    .map(Day15::runHashFunction)
                    .reduce(Integer::sum)
                    .orElse(0);
            System.out.println(res);
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day15.class.getResource("day15.txt").toURI()))) {
            String[] parts = stream.toList().get(0).split(",");
            Box[] boxes = IntStream.range(0, 256).mapToObj(i -> new Box()).toArray(Box[]::new);
            for (String part : parts) {
                String[] splitted = part.split("[=\\-]");
                String label = splitted[0];
                int box = runHashFunction(label);
                if (splitted.length > 1) { // =
                    int focalLength = Integer.parseInt(splitted[1]);
                    boxes[box].addLensIfNotExist(new LabeledLens(label, focalLength));
                } else {  // -
                    boxes[box].removeLens(label);
                }
            }
            System.out.println(calculateScore(boxes));
        }
    }

    private static int calculateScore(Box[] boxes) {
        int result = 0;
        for (int i = 0; i < boxes.length; i++) {
            List<LabeledLens> lenses = boxes[i].getLenses();
            int lensScore = 0;
            for (int j = 0; j < lenses.size(); j++) {
                lensScore += (i + 1) * (j + 1) * lenses.get(j).focalLength();
            }
            result += lensScore;
        }
        return result;
    }

    private record LabeledLens(String label, Integer focalLength) {
    }

    private static class Box {
        private List<LabeledLens> lenses;

        public Box() {
            lenses = new LinkedList<>();
        }

        public List<LabeledLens> getLenses() {
            return lenses;
        }

        public void removeLens(String label) {
            lenses.removeIf(lens -> lens.label().equals(label));
        }

        public void addLensIfNotExist(LabeledLens lens) {
            ListIterator<LabeledLens> iterator = lenses.listIterator();
            while (iterator.hasNext()) {
                LabeledLens existing = iterator.next();
                if (existing.label().equals(lens.label())) {
                    iterator.set(lens);
                    return;
                }
            }
            lenses.add(lens);
        }
    }

    private static int runHashFunction(String input) {
        int value = 0;
        for (int i = 0; i < input.length(); i++) {
            value += input.charAt(i);
            value *= 17;
            value = value % 256;
        }
        return value;
    }
}
