import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day5 {

    public static void main(String[] args) throws Exception {
        SimpleProfiler profiler = new SimpleProfiler().start();
        solvePart1();
        profiler.stop();

        profiler.start();
        solvePart2();
        profiler.stop();
    }

    private static void solvePart1() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Day5.class.getResourceAsStream("day5.txt")))) {
            String line = reader.readLine();
            Set<Long> seeds = Arrays.stream(line.split(":")[1].trim().split("\\s+")).map(Long::parseLong).collect(Collectors.toSet());

            List<Section> sections = createSections(reader, false);

            System.out.println(traverseSections(sections, seeds));
        }
    }

    private static List<Section> createSections(BufferedReader reader, boolean reversed) throws IOException {
        String line;
        List<Section> sections = new ArrayList<>();
        List<String> section = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                if (!Character.isDigit(line.charAt(0))) {
                    continue;
                }
                section.add(line);
            } else if (!section.isEmpty()) {
                sections.add(convert(section, reversed));
                section = new ArrayList<>();
            }
        }
        sections.add(convert(section, reversed));
        if (reversed) {
            Collections.reverse(sections);
        }
        return sections;
    }


    private static long traverseSections(List<Section> sections, Set<Long> seeds) {
        List<Long> results = new ArrayList<>();
        for (Long seed : seeds) {
            results.add(traverseOneSeed(sections, seed));
        }
        return results.stream().reduce(Long::min).orElse(0L);
    }

    private static long traverseOneSeed(List<Section> sections, Long seed) {
        long newSeed = seed;
        for (Section section : sections) {
            for (NumberRangeMapping numberRangeMapping : section.ranges()) {
                if (numberRangeMapping.isWithinSourceRange(newSeed)) {
                    newSeed = numberRangeMapping.getDest(newSeed);
                    break;
                }
            }
        }
        return newSeed;
    }

    private static Section convert(List<String> sectionStringList, boolean reversed) {
        Set<NumberRangeMapping> ranges = new HashSet<>();
        for (String line : sectionStringList) {
            int sourceIndex = reversed ? 0 : 1;
            int destIndex = reversed ? 1 : 0;
            ranges.add(new NumberRangeMapping(
                    Long.parseLong(line.split(" ")[destIndex]),
                    Long.parseLong(line.split(" ")[sourceIndex]),
                    Long.parseLong(line.split(" ")[2])));
        }
        return new Section(ranges);
    }

    private record NumberRangeMapping(long dest, long source, long rangeLength) {
        public boolean isWithinSourceRange(long nbr) {
            return nbr >= source && nbr <= source + (rangeLength - 1);
        }

        public Long getDest(long nbr) {
            long diff = source + (rangeLength - 1) - nbr;
            return (dest + (rangeLength - 1)) - diff;
        }
    }

    private record Section(Set<NumberRangeMapping> ranges) {
    }


    private static void solvePart2() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Day5.class.getResourceAsStream("day5.txt")))) {
            String line = reader.readLine();
            List<Long> allSeeds = Arrays.stream(line.split(":")[1].trim().split("\\s+")).map(Long::parseLong).toList();
            Set<Range> seeds = new HashSet<>();
            for (int i = 0; i < allSeeds.size(); i = i + 2) {
                long from = allSeeds.get(i);
                long to = allSeeds.get(i + 1);
                seeds.add(new Range(from, from + (to - 1)));
            }

            List<Section> sections = createSections(reader, true);

            for (long j = 0; j < Long.MAX_VALUE; j++) {
                long result = traverseOneSeed(sections, j);
                if (seeds.stream().anyMatch(seed -> seed.isWithin(result))) {
                    System.out.println(j);
                    break;
                }
            }

            // Chunk up, check all sections for matching range? Divide and conquer
            // calculate down to the smallest range possible for each of the ranges.
            // NumberRangeMapping#isWithinSourceRange(Range range)
            // private record Range(long from, long to) (inclusive range, [from,to])

            // [A - B]
            // [A - (A-B/2) ] , [ (A-B/2) - B]
            // etc....

            // Then same logic as part1?
        }
    }

    private record Range(long from, long to) {
        public boolean isWithin(long nbr) {
            return nbr >= from && nbr <= to;
        }
    }

}
