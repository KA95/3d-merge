package com.bsu.klimansky.util;

import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Anton Klimansky on 09.05.2017.
 */
class GraphUtil {

    static Map<Integer, Set<Integer>> buildGraphByMesh(TriangulatedMesh mesh) {
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        for (Triangle t : mesh.triangles) {
            addEdge(graph, t.getP1(), t.getP2());
            addEdge(graph, t.getP2(), t.getP3());
            addEdge(graph, t.getP1(), t.getP3());
        }
        return graph;
    }

    private static void addEdge(Map<Integer, Set<Integer>> graph, int v1, int v2) {
        addEdgeInternal(graph, v1, v2);
        addEdgeInternal(graph, v2, v1);
    }

    private static void addEdgeInternal(Map<Integer, Set<Integer>> graph, int v1, int v2) {
        if (graph.containsKey(v1)) {
            graph.get(v1).add(v2);
        } else {
            Set<Integer> set = new HashSet<>();
            set.add(v2);
            graph.put(v1, set);
        }
    }

}
