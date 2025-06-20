package algorithm;

import model.GraphNode;

import java.util.*;

public class Dijkstra {
    public static Map<String, Integer> findShortestPaths(GraphNode start) {
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> distances.getOrDefault(node.getName(), Integer.MAX_VALUE)));

        distances.put(start.getName(), 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            int currentDist = distances.get(current.getName());

            for (Map.Entry<GraphNode, Integer> neighbor : current.getNeighbors().entrySet()) {
                GraphNode next = neighbor.getKey();
                int weight = neighbor.getValue();
                int newDist = currentDist + weight;

                if (newDist < distances.getOrDefault(next.getName(), Integer.MAX_VALUE)) {
                    distances.put(next.getName(), newDist);
                    queue.add(next);
                }
            }
        }

        return distances;
    }
}
