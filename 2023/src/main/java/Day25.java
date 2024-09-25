import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class Day25 {

    public static void main(String[] args) throws Exception {
        solvePart1();
    }

    // Credit to /r/adventofcode/ and yfilipov for this solution
    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day25.class.getResource("day25.txt").toURI()))) {
            Map<String, Set<String>> graph = setUpGraph(stream.toList());
            // Create Graphviz dot language representation of graph
            String dot = createDotFormat(graph);
            System.out.println(dot);
            // Visualizing the dot file in Gephi gives the following 3 links to remove:
            // nvt -- zdj
            // mzg -- bbm
            // cth -- xxk
            Optional.ofNullable(graph.get("nvt")).ifPresent(list -> list.remove("zdj"));
            Optional.ofNullable(graph.get("zdj")).ifPresent(list -> list.remove("nvt"));

            Optional.ofNullable(graph.get("mzg")).ifPresent(list -> list.remove("bbm"));
            Optional.ofNullable(graph.get("bbm")).ifPresent(list -> list.remove("mzg"));

            Optional.ofNullable(graph.get("cth")).ifPresent(list -> list.remove("xxk"));
            Optional.ofNullable(graph.get("xxk")).ifPresent(list -> list.remove("cth"));

            addLinksBack(graph);
            // Now run BFS on one node to get the size of group 1, size of group 2 is the remaining nodes
            int totalNbrOfNodes = getTotalNbrOfNodes(graph);
            int group1Size = bfs("vqs", graph);
            System.out.println(group1Size * (totalNbrOfNodes - group1Size));
        }
    }

    private static int getTotalNbrOfNodes(Map<String, Set<String>> graph) {
        Set<String> nodes = new HashSet<>();
        for (String key : graph.keySet()) {
            nodes.add(key);
            nodes.addAll(graph.get(key));
        }
        return nodes.size();
    }

    private static void addLinksBack(Map<String, Set<String>> graph) {
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            for (String conn : entry.getValue()) {
                Optional.ofNullable(graph.get(conn)).ifPresent(l -> l.add(entry.getKey()));
            }
        }
    }

    private static int bfs(String start, Map<String, Set<String>> graph) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            String node = queue.poll();
            if (visited.contains(node)) {
                continue;
            }
            visited.add(node);
            Optional.ofNullable(graph.get(node)).ifPresent(queue::addAll);
        }
        return visited.size();
    }

    private static Map<String, Set<String>> setUpGraph(List<String> lines) {
        Map<String, Set<String>> graph = new HashMap<>();
        for (String line : lines) {
            String part = line.split(":")[0];
            Set<String> connections = Arrays.stream(line.split(":")[1].trim().split(" ")).collect(toCollection(HashSet::new));
            graph.put(part, connections);
        }
        return graph;
    }

    private static String createDotFormat(Map<String, Set<String>> graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("graph {\n");
        graph.forEach((key, value) -> value.forEach(v -> sb.append(key).append(" -- ").append(v).append(";\n")));
        sb.append("}");
        return sb.toString();
    }


}
