import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {

    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day20.class.getResource("day20.txt").toURI()))) {
            List<String> lines = stream.toList();

            Map<String, List<String>> conjNameToInputsMap = getConjunctionModuleNames(lines)
                    .stream()
                    .collect(Collectors.toMap(n -> n, n -> new ArrayList<>()));
            Map<String, Module> moduleMap = new HashMap<>();
            fillBroadcasterAndFlipFlopModules(lines, conjNameToInputsMap, moduleMap);
            finalizeConjunctionModules(lines, conjNameToInputsMap, moduleMap);

            int countLows = 0;
            int countHighs = 0;
            for (int i = 0; i < 1000; i++) {
                Queue<Pulse> queue = new LinkedList<>();
                Pulse startPulse = new Pulse(0, "button", "broadcaster");
                queue.add(startPulse);

                while (!queue.isEmpty()) {
                    Pulse pulse = queue.poll();
                    if (pulse.lowHigh() == 1) {
                        countHighs++;
                    } else {
                        countLows++;
                    }
                    Module module = moduleMap.get(pulse.destinationModule());
                    if (module != null) {
                        List<Pulse> outgoingPulses = module.run(pulse);
                        queue.addAll(outgoingPulses);
                    }
                }
            }
            System.out.println(countLows * countHighs);
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day20.class.getResource("day20.txt").toURI()))) {
            List<String> lines = stream.toList();

            Map<String, List<String>> conjNameToInputsMap = getConjunctionModuleNames(lines)
                    .stream()
                    .collect(Collectors.toMap(n -> n, n -> new ArrayList<>()));
            Map<String, Module> moduleMap = new HashMap<>();
            fillBroadcasterAndFlipFlopModules(lines, conjNameToInputsMap, moduleMap);
            finalizeConjunctionModules(lines, conjNameToInputsMap, moduleMap);

            long cycleDs = 0, cycleDt = 0, cycleBd = 0, cycleCs = 0;
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                Queue<Pulse> queue = new LinkedList<>();
                Pulse startPulse = new Pulse(0, "button", "broadcaster");
                queue.add(startPulse);

                while (!queue.isEmpty()) {
                    Pulse pulse = queue.poll();
                    Module module = moduleMap.get(pulse.destinationModule());
                    if (module != null) {
                        List<Pulse> outgoingPulses = module.run(pulse);
                        queue.addAll(outgoingPulses);
                    }
                    // Find cycles for &ds,&dt,&bd,&cs
                    // These are the ones inputting to &bd,&cl,&tn,&dr which in turn connect to &vr -> rx
                    if (allOnes((Conjunction) moduleMap.get("ds"))) {
                        cycleDs = i;
                    }
                    if (allOnes((Conjunction) moduleMap.get("dt"))) {
                        cycleDt = i;
                    }
                    if (allOnes((Conjunction) moduleMap.get("bd"))) {
                        cycleBd = i;
                    }
                    if (allOnes((Conjunction) moduleMap.get("cs"))) {
                        cycleCs = i;
                    }
                }
                if (cycleDs != 0 && cycleDt != 0 && cycleBd != 0 && cycleCs != 0) {
                    List<Long> nbrs = List.of(cycleDs, cycleDt, cycleBd, cycleCs);
                    long gcd = getGCD(cycleDs, cycleDt, cycleBd, cycleCs);
                    System.out.println(leastCommonMultipleOfList(nbrs, gcd));
                    return;
                }
            }
        }
    }

    private static long leastCommonMultipleOfList(List<Long> input, long gcd) {
        long result = input.get(0);
        for (int i = 1; i < input.size(); i++)
            result = leastCommonMultiple(result, input.get(i), gcd);
        return result;
    }

    private static long leastCommonMultiple(long a, long b, long gcd) {
        return a * (b / gcd);
    }

    private static long getGCD(long a, long b, long c, long d) {
        BigInteger gcdAB = BigInteger.valueOf(a).gcd(BigInteger.valueOf(b));
        BigInteger gcdABC = gcdAB.gcd(BigInteger.valueOf(c));
        BigInteger gcdABCD = gcdABC.gcd(BigInteger.valueOf(d));
        return gcdABCD.longValue();
    }

    private static boolean allOnes(Conjunction module) {
        return module.getInputOnOffMap().values().stream().allMatch(b -> b);
    }


    private static List<String> getConjunctionModuleNames(List<String> lines) {
        return lines.stream()
                .map(Day20::getFirstPart)
                .filter(firstPart -> !firstPart.equals("broadcaster") && firstPart.charAt(0) == '&')
                .map(firstPart -> firstPart.substring(1))
                .toList();
    }

    private static String getFirstPart(String line) {
        String[] split = line.split("->");
        return split[0].trim();
    }

    private static List<String> getTargets(String line) {
        String[] split = line.split("->");
        return Arrays.stream(split[1].trim().split(",")).map(String::trim).toList();
    }

    private static void fillBroadcasterAndFlipFlopModules(List<String> lines,
                                                          Map<String, List<String>> conjunctionMapping,
                                                          Map<String, Module> moduleMap) {
        for (String line : lines) {
            String firstPart = getFirstPart(line);
            List<String> targets = getTargets(line);
            if (firstPart.equals("broadcaster")) {
                handleBroadCasterLine(conjunctionMapping, moduleMap, targets);
            } else {
                String moduleName = firstPart.substring(1);
                if (firstPart.charAt(0) == '%') {
                    moduleMap.put(moduleName, new FlipFlop(moduleName, targets));
                }
                for (String target : targets) {
                    if (conjunctionMapping.get(target) != null) {
                        conjunctionMapping.get(target).add(moduleName);
                    }
                }
            }
        }
    }

    private static void handleBroadCasterLine(Map<String, List<String>> conjunctionMapping,
                                              Map<String, Module> moduleMap,
                                              List<String> targets) {
        moduleMap.put("broadcaster", new Broadcaster("broadcaster", targets));
        for (String target : targets) {
            if (conjunctionMapping.get(target) != null) {
                conjunctionMapping.get(target).add("broadcaster");
            }
        }
    }

    private static void finalizeConjunctionModules(List<String> lines,
                                                   Map<String, List<String>> conjunctionMapping,
                                                   Map<String, Module> moduleMap) {
        for (String line : lines) {
            String firstPart = getFirstPart(line);
            List<String> targets = getTargets(line);
            if (!firstPart.equals("broadcaster")) {
                String moduleName = firstPart.substring(1);
                if (firstPart.charAt(0) == '&') {
                    List<String> inputModules = conjunctionMapping.get(moduleName);
                    moduleMap.put(moduleName, new Conjunction(moduleName, inputModules, targets));
                }
            }
        }
    }

    private interface Module {
        List<Pulse> run(Pulse incoming);
    }

    private static class FlipFlop implements Module {
        private final String name;
        private boolean onOff = false;
        private final List<String> targetModules;

        public FlipFlop(String name, List<String> targetModules) {
            this.name = name;
            this.targetModules = targetModules;
        }

        @Override
        public List<Pulse> run(Pulse incoming) {
            if (incoming.lowHigh() == 0) {
                onOff = !onOff;
                return this.targetModules.stream().map(target -> new Pulse(onOff ? 1 : 0, name, target)).toList();
            }
            return Collections.emptyList();
        }
    }

    private static class Conjunction implements Module {
        private final String name;
        private final Map<String, Boolean> inputOnOffMap;
        private final List<String> targetModules;

        public Conjunction(String name, List<String> inputModules, List<String> targetModules) {
            this.name = name;
            this.inputOnOffMap = new HashMap<>();
            for (String inputModule : inputModules) {
                this.inputOnOffMap.put(inputModule, false);
            }
            this.targetModules = targetModules;
        }

        public Map<String, Boolean> getInputOnOffMap() {
            return Collections.unmodifiableMap(inputOnOffMap);
        }

        @Override
        public List<Pulse> run(Pulse incoming) {
            String incomingModule = incoming.fromModule();
            this.inputOnOffMap.compute(incomingModule, (k, v) -> incoming.lowHigh() == 1);
            int outSignal = this.inputOnOffMap.values().stream().allMatch(b -> b) ? 0 : 1;
            return this.targetModules.stream().map(target -> new Pulse(outSignal, name, target)).toList();
        }
    }

    private record Broadcaster(String name, List<String> targetModules) implements Module {

        @Override
        public List<Pulse> run(Pulse incoming) {
            return this.targetModules.stream().map(target -> new Pulse(incoming.lowHigh(), name, target)).toList();
        }
    }

    private record Pulse(int lowHigh, String fromModule, String destinationModule) {
    }

}
