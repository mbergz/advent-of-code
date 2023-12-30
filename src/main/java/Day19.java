import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day19 {
    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day19.class.getResource("day19.txt").toURI()))) {
            List<String> lines = stream.toList();

            Map<String, WorkFlow> workFlows = new HashMap<>();
            for (String workflowLine : lines.subList(0, lines.indexOf(""))) {
                addWorkFlow(workflowLine, workFlows);
            }
            List<Part> parts = createParts(lines.subList(lines.indexOf("") + 1, lines.size()));

            List<Part> acceptedParts = new ArrayList<>();
            WorkFlow startFlow = workFlows.get("in");
            for (Part part : parts) {
                String result = startFlow.run(part);
                while (!result.equals("A") && !result.equals("R")) {
                    result = workFlows.get(result).run(part);
                }
                if (result.equals("A")) {
                    acceptedParts.add(part);
                }
            }
            System.out.println(acceptedParts.stream().map(part -> part.x() + part.m() + part.a() + part.s()).reduce(Integer::sum).orElse(0));
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day19.class.getResource("day19.txt").toURI()))) {
            List<String> lines = stream.toList();

            Map<String, WorkFlow> workFlows = new HashMap<>();
            for (String workflowLine : lines.subList(0, lines.indexOf(""))) {
                addWorkFlow(workflowLine, workFlows);
            }
            WorkFlow startFlow = workFlows.get("in");
            Ranges startRanges = createStartRanges();

            // All Ranges are unique/disjoint
            List<Ranges> result = getAllValidRanges(workFlows, startFlow, startRanges);
            long score = 0L;
            for (Ranges ranges : result) {
                score += getScoreForRanges(ranges);
            }
            System.out.println(score);
        }
    }

    private static long getScoreForRanges(Ranges ranges) {
        long xScore = ranges.x().to() - ranges.x().from() + 1;
        long mScore = ranges.m().to() - ranges.m().from() + 1;
        long aScore = ranges.a().to() - ranges.a().from() + 1;
        long sScore = ranges.s().to() - ranges.s().from() + 1;
        return xScore * mScore * aScore * sScore;
    }

    private static Ranges createStartRanges() {
        Range x = new Range(1, 4000);
        Range m = new Range(1, 4000);
        Range a = new Range(1, 4000);
        Range s = new Range(1, 4000);
        return new Ranges(x, m, a, s);
    }

    private static List<Ranges> getAllValidRanges(Map<String, WorkFlow> workFlowMap, WorkFlow current, Ranges ranges) {
        List<Ranges> result = new ArrayList<>();
        Ranges currentRanges = ranges;
        for (ConditionEvaluator evaluator : current.evaluators()) {
            PairRanges pairRanges = evaluator.evaluateRanges(currentRanges);
            if (pairRanges != null) {
                if (pairRanges.pass() != null) {
                    if (evaluator.nextWorkflow().equals("A")) {
                        result.add(pairRanges.pass());
                    } else if (!evaluator.nextWorkflow().equals("R")) {
                        result.addAll(getAllValidRanges(workFlowMap, workFlowMap.get(evaluator.nextWorkflow()), pairRanges.pass()));
                    }
                }
                if (pairRanges.fail() != null) {
                    currentRanges = pairRanges.fail();
                }
            }
        }
        if (current.fallback().equals("A")) {
            result.add(currentRanges);
        } else if (!current.fallback().equals("R")) {
            result.addAll(getAllValidRanges(workFlowMap, workFlowMap.get(current.fallback()), currentRanges));
        }
        return result;
    }

    private static List<Part> createParts(List<String> partLines) {
        List<Part> parts = new ArrayList<>();
        for (String partLine : partLines) {
            List<Integer> partSplit = Arrays.stream(partLine.substring(1, partLine.length() - 1).split(","))
                    .map(category -> category.split("=")[1])
                    .map(Integer::parseInt)
                    .toList();
            parts.add(new Part(partSplit.get(0), partSplit.get(1), partSplit.get(2), partSplit.get(3)));
        }
        return parts;
    }

    private static void addWorkFlow(String line, Map<String, WorkFlow> workFlows) {
        String name = line.split("\\{")[0];
        String conditionLine = line.split("\\{")[1];
        String[] conditions = conditionLine.substring(0, conditionLine.length() - 1).split(",");
        List<ConditionEvaluator> evaluators = new ArrayList<>();
        for (int i = 0; i < conditions.length - 1; i++) {
            evaluators.add(createConditionEvaluator(conditions[i]));
        }
        String last = conditions[conditions.length - 1];
        workFlows.put(name, new WorkFlow(evaluators, last));
    }

    private static ConditionEvaluator createConditionEvaluator(String conditionLine) {
        boolean moreThan = conditionLine.charAt(1) == '>';
        int nbr = Integer.parseInt(conditionLine.split("[<>]")[1].split(":")[0]);
        String nextWorkFlow = conditionLine.split(":")[1];
        return new ConditionEvaluator(conditionLine.charAt(0), moreThan, nbr, nextWorkFlow);
    }


    private record ConditionEvaluator(char category, boolean moreThan, int nbr, String nextWorkflow) {
        String evaluate(Part part) {
            int nbrToCompare = switch (this.category) {
                case 'x' -> part.x();
                case 'm' -> part.m();
                case 'a' -> part.a();
                case 's' -> part.s();
                default -> throw new IllegalStateException("Unexpected value: " + this.category);
            };
            if ((moreThan && nbrToCompare > this.nbr) || (!moreThan && nbrToCompare < this.nbr)) {
                return this.nextWorkflow;
            }
            return null;
        }

        PairRanges evaluateRanges(Ranges ranges) {
            Ranges passRange;
            Ranges failRange = null;
            Range rangeToModify = getRangeToModify(ranges);
            if ((this.moreThan && rangeToModify.from() > this.nbr) ||
                    (!this.moreThan && (rangeToModify.from() > this.nbr))) {
                passRange = ranges;
            } else if ((this.moreThan && rangeToModify.to() <= this.nbr) ||
                    (!this.moreThan && rangeToModify.from() >= this.nbr)) {
                return null;
            } else {
                passRange = this.moreThan ?
                        getNewRange(ranges, new Range(this.nbr() + 1, rangeToModify.to())) :
                        getNewRange(ranges, new Range(rangeToModify.from(), this.nbr() - 1));
                failRange = this.moreThan ?
                        getNewRange(ranges, new Range(rangeToModify.from(), this.nbr())) :
                        getNewRange(ranges, new Range(this.nbr(), rangeToModify.to()));
            }
            return new PairRanges(passRange, failRange);
        }

        private Range getRangeToModify(Ranges ranges) {
            return switch (this.category) {
                case 'x' -> ranges.x();
                case 'm' -> ranges.m();
                case 'a' -> ranges.a();
                case 's' -> ranges.s();
                default -> throw new IllegalStateException("Unexpected value: " + this.category);
            };
        }

        private Ranges getNewRange(Ranges ranges, Range newPassRange) {
            return switch (this.category) {
                case 'x' -> new Ranges(newPassRange, ranges.m(), ranges.a(), ranges.s());
                case 'm' -> new Ranges(ranges.x(), newPassRange, ranges.a(), ranges.s());
                case 'a' -> new Ranges(ranges.x(), ranges.m(), newPassRange, ranges.s());
                case 's' -> new Ranges(ranges.x(), ranges.m(), ranges.a(), newPassRange);
                default -> throw new IllegalStateException("Unexpected value: " + this.category);
            };
        }
    }

    // [from,to]
    private record Range(int from, int to) {
    }

    private record Ranges(Range x, Range m, Range a, Range s) {
    }

    private record PairRanges(Ranges pass, Ranges fail) {
    }

    private record WorkFlow(List<ConditionEvaluator> evaluators, String fallback) {

        String run(Part part) {
            for (ConditionEvaluator evaluator : this.evaluators) {
                String res = evaluator.evaluate(part);
                if (res != null) {
                    return res;
                }
            }
            return this.fallback;
        }
    }

    private record Part(int x, int m, int a, int s) {
    }
}
