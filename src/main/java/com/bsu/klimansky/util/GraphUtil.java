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

    static Map<Integer, Map<Integer, Integer>> calculateBoundEdges(TriangulatedMesh mesh) {
        Map<Integer, Map<Integer, Integer>> graph = new HashMap<>();
        for (Triangle t : mesh.triangles) {
            addEdge(graph, t.getP1(), t.getP2());
            addEdge(graph, t.getP2(), t.getP3());
            addEdge(graph, t.getP1(), t.getP3());
        }
        return graph;
    }
    private static void addEdge(Map<Integer, Map<Integer, Integer>> graph, int v1, int v2) {
        addEdgeInternal(graph, v1, v2);
        addEdgeInternal(graph, v2, v1);
    }
    private static void addEdgeInternal(Map<Integer, Map<Integer, Integer>> graph, int v1, int v2) {
        if (graph.containsKey(v1)) {
            if (graph.get(v1).containsKey(v2)) {
                int c = graph.get(v1).get(v2);
                graph.get(v1).put(v2, c + 1);
            } else {
                graph.get(v1).put(v2, 1);
            }
        } else {
            Map<Integer, Integer> map = new HashMap<>();
            map.put(v2, 1);
            graph.put(v1, map);
        }
    }

}
