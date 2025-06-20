package model;

import java.util.*;

public class GraphNode {
    private String name;
    private Map<GraphNode, Integer> neighbors;

    public GraphNode(String name) {
        this.name = name;
        this.neighbors = new HashMap<>();
    }

    public void addNeighbor(GraphNode node, int distance) {
        neighbors.put(node, distance);
    }

    public String getName() { return name; }
    public Map<GraphNode, Integer> getNeighbors() { return neighbors; }
}
