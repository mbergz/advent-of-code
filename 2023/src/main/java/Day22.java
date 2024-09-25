import Common.Range;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {

    public static void main(String[] args) throws Exception {
        SimpleProfiler profiler = new SimpleProfiler().start();
        solvePart1();
        profiler.stop();
        profiler.start();
        solvePart2();
        profiler.stop();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day22.class.getResource("day22.txt").toURI()))) {
            List<String> lines = stream.toList();
            List<Block> allBlocks = lines.stream().map(Block::new).toList();

            List<Block> result = runFallingDownAlgorithm(allBlocks);
            addSupportingForAlLBlocks(result);

            // If block is only supporting one other block, it cannot be removed
            Set<Block> uniqueSupporters = new HashSet<>();
            for (Block block : result) {
                if (block.getSupportingBlocks().size() == 1) {
                    uniqueSupporters.add(block.getSupportingBlocks().get(0));
                }
            }
            System.out.println(allBlocks.size() - uniqueSupporters.size());
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day22.class.getResource("day22.txt").toURI()))) {
            List<String> lines = stream.toList();
            List<Block> allBlocks = lines.stream().map(Block::new).toList();

            List<Block> result = runFallingDownAlgorithm(allBlocks);
            addSupportingForAlLBlocks(result);

            Set<Block> uniqueSupporters = new HashSet<>();
            for (Block block : result) {
                if (block.getSupportingBlocks().size() == 1) {
                    uniqueSupporters.add(block.getSupportingBlocks().get(0));
                }
            }

            int count = 0;
            Map<Block, List<Block>> supportingMap = createSupportingMap(result);
            for (Block uniqueSupporter : uniqueSupporters) {
                count += recursiveCountAllFallen(uniqueSupporter, supportingMap, new HashSet<>());
            }
            System.out.println(count);
        }
    }

    private static Map<Block, List<Block>> createSupportingMap(List<Block> all) {
        Map<Block, List<Block>> map = new HashMap<>();
        for (Block block : all) {
            map.put(block, new ArrayList<>());
            List<Block> supportingBlocks = block.getSupportingBlocks();
            for (Block supp : supportingBlocks) {
                map.compute(supp, (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.add(block);
                    return v;
                });
            }
        }
        return map;
    }

    private static int recursiveCountAllFallen(Block current, Map<Block, List<Block>> supportingMap, Set<Block> alreadyFallen) {
        alreadyFallen.add(current);
        Set<Block> supportedByThis = Optional.ofNullable(supportingMap.get(current))
                .map(blocksAbove -> blocksAbove.stream().filter(b -> allSupportingBlocksGone(alreadyFallen, b)).collect(Collectors.toSet()))
                .orElseGet(Collections::emptySet);
        int count = supportedByThis.size();
        if (count != 0) {
            for (Block supported : supportedByThis)
                count += recursiveCountAllFallen(supported, supportingMap, alreadyFallen);
        }
        return count;
    }

    private static boolean allSupportingBlocksGone(Set<Block> alreadyFallen, Block b) {
        return alreadyFallen.containsAll(b.getSupportingBlocks());
    }

    private static void addSupportingForAlLBlocks(List<Block> allBlocks) {
        for (Block block : allBlocks) {
            if (block.z().from() > 1) {
                findSupportingBlocks(block, allBlocks).forEach(block::addSupportingBlock);
            }
        }
    }

    private static List<Block> findSupportingBlocks(Block block, List<Block> allBlocks) {
        return allBlocks.stream().filter(b -> b.z().to() == block.z().from() - 1)
                .filter(b -> matchesXandY(b, block)).toList();
    }

    private static boolean matchesXandY(Block b1, Block b2) {
        return b1.x().isInRange(b2.x()) && b1.y().isInRange(b2.y());
    }

    private static List<Block> runFallingDownAlgorithm(List<Block> allBlocks) {
        PriorityQueue<Block> queue = new PriorityQueue<>();
        queue.addAll(allBlocks);

        List<Block> resultingBlocks = new ArrayList<>();
        while (!queue.isEmpty()) {
            insertBlock(resultingBlocks, queue.poll());
        }
        return resultingBlocks;
    }

    private static void insertBlock(List<Block> resultingBlocks, Block blockToInsert) {
        List<Block> intersectingBlocks = getIntersectingBlocks(resultingBlocks, blockToInsert);
        int height = blockToInsert.z().to() - blockToInsert.z().from();

        if (!intersectingBlocks.isEmpty()) {
            int newMinZ = intersectingBlocks.stream().map(Block::z).map(Range::to).reduce(Integer::max).orElse(0) + 1;
            Range newZ = new Range(newMinZ, newMinZ + height);
            resultingBlocks.add(new Block(blockToInsert.id(), blockToInsert.x(), blockToInsert.y(), newZ));
        } else {
            if (blockToInsert.z().from() == 1) {
                resultingBlocks.add(blockToInsert);
                return;
            }
            int currentLowZIndex = blockToInsert.z().from();
            for (int z = currentLowZIndex - 1; z >= 1; z--) {
                Range newZ = new Range(z, z + height);
                Block testBlock = new Block(blockToInsert.id(), blockToInsert.x(), blockToInsert.y(), newZ);
                if (z == 1 && getIntersectingBlocks(resultingBlocks, testBlock).isEmpty()) {
                    resultingBlocks.add(testBlock);
                    return;
                }
                if (!getIntersectingBlocks(resultingBlocks, testBlock).isEmpty()) {
                    resultingBlocks.add(blockToInsert);
                    return;
                }
                blockToInsert = testBlock;
            }
        }
    }

    private static List<Block> getIntersectingBlocks(List<Block> resultingBlocks, Block blockToInsert) {
        List<Block> existingIntersectingBlocks = new ArrayList<>();
        for (Block resulting : resultingBlocks) {
            if (intersects(resulting, blockToInsert)) {
                existingIntersectingBlocks.add(resulting);
            }
        }
        return existingIntersectingBlocks;
    }

    private static boolean intersects(Block existingBlock, Block blockToInsert) {
        if (existingBlock.z().to() >= blockToInsert.z().from()) {
            return blockToInsert.x().isInRange(existingBlock.x()) &&
                    blockToInsert.y().isInRange(existingBlock.y());
        }
        return false;
    }

    private static class Block implements Comparable<Block> {
        private final String id;
        private final Range x;
        private final Range y;
        private final Range z;
        private final List<Block> supportingBlocks = new ArrayList<>();

        private Block(String id, Range x, Range y, Range z) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Block(String line) {
            this(UUID.randomUUID().toString(),
                    new Range(getFirstPartAtIndex(line, 0), getSecondPartAtIndex(line, 0)),
                    new Range(getFirstPartAtIndex(line, 1), getSecondPartAtIndex(line, 1)),
                    new Range(getFirstPartAtIndex(line, 2), getSecondPartAtIndex(line, 2)));
        }


        public void addSupportingBlock(Block supporting) {
            this.supportingBlocks.add(supporting);
        }

        public List<Block> getSupportingBlocks() {
            return this.supportingBlocks;
        }

        public String id() {
            return id;
        }

        public Range x() {
            return x;
        }

        public Range y() {
            return y;
        }

        public Range z() {
            return z;
        }

        @Override
        public int compareTo(Block o) {
            return Integer.compare(this.z.from(), o.z().from());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Block block = (Block) o;
            return Objects.equals(id, block.id) && Objects.equals(x, block.x) && Objects.equals(y, block.y) && Objects.equals(z, block.z);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, x, y, z);
        }
    }


    private static int getFirstPartAtIndex(String line, int index) {
        return Integer.parseInt(line.split("~")[0].split(",")[index]);
    }

    private static int getSecondPartAtIndex(String line, int index) {
        return Integer.parseInt(line.split("~")[1].split(",")[index]);
    }

}
