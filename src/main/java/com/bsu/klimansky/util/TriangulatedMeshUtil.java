package com.bsu.klimansky.util;

import com.bsu.klimansky.model.AdvancedTriangulatedMesh;
import com.bsu.klimansky.model.Edge;
import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;

import java.util.*;

import static com.bsu.klimansky.util.GraphUtil.calculateBoundEdges;

/**
 * Created by Anton Klimansky on 09.05.2017.
 */
public class TriangulatedMeshUtil {

    public static TriangulatedMesh visualizeBound(TriangulatedMesh mesh, List<Integer> bound, boolean upper) {
        TriangulatedMesh result = new TriangulatedMesh();
        List<Point3D> points = mesh.points;

        if (upper) {
            points.add(new Point3D(0, 0.03, 0));
        } else {
            points.add(new Point3D(0, 0.1, 0));
        }
        result.points = points;

        List<Triangle> triangles = new ArrayList<>();
        for (int i = 0; i < bound.size() - 1; i++) {
            if (upper) {
                triangles.add(new Triangle(bound.get(i), bound.get(i + 1), points.size() - 1));
            } else {
                triangles.add(new Triangle(points.size() - 1, bound.get(i + 1), bound.get(i)));
            }
        }
        result.triangles = triangles;
        return result;
    }

    public static List<Integer> boundForMesh(TriangulatedMesh mesh, boolean upper) {
        // build graph
        //v1->v2->counter of usages in triangulation
        Map<Integer, Map<Integer, Integer>> graph = calculateBoundEdges(mesh);
        Set<Integer> boundSet = new HashSet<>();
        Set<Edge> boundEdges = new HashSet<>();

        List<Integer> bound = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> e1 : graph.entrySet()) {
            for (Map.Entry<Integer, Integer> e2 : e1.getValue().entrySet()) {
                if (e2.getValue() == 1 && e1.getKey() < e2.getKey()) {
                    boundEdges.add(new Edge(e1.getKey(), e2.getKey()));
                    boundEdges.add(new Edge(e2.getKey(), e1.getKey()));
                    if (!boundSet.contains(e1.getKey())) boundSet.add(e1.getKey());
                    if (!boundSet.contains(e2.getKey())) boundSet.add(e2.getKey());
                }
            }
        }

        //get first point
        int first = getFirstPoint(mesh, upper);
        boolean[] used = new boolean[mesh.points.size()];
        for (int i = 0; i < mesh.points.size(); i++)
            used[i] = false;
        int cur = first;
        boolean hasMore = true;
        while (hasMore) {
            hasMore = false;
            bound.add(cur);
            used[cur] = true;
            Set<Map.Entry<Integer, Integer>> edges = graph.containsKey(cur) ? graph.get(cur).entrySet() : null;
            if (edges != null) {
                for (Map.Entry<Integer, Integer> m : edges) {
                    if (boundSet.contains(m.getKey()) && !used[m.getKey()]) {
                        if (boundEdges.contains(new Edge(cur, m.getKey())) || boundEdges.contains(new Edge(m.getKey(), cur))) {
                            cur = m.getKey();
                            hasMore = true;
                            break;
                        }
                    }
                }
            }
        }
        bound.add(first);
        return bound;
    }

    private static int getFirstPoint(TriangulatedMesh mesh, boolean upper) {
        int pFirstIndex = 0;
        List<Point3D> points = mesh.points;
        Point3D pFirst = points.get(0);

        for (int i = 1; i < points.size(); i++) {
            Point3D point = points.get(i);
            if (upper) {
                if (point.getY() < pFirst.getY()) {
                    pFirst = point;
                    pFirstIndex = i;
                }
            } else {
                if (point.getY() > pFirst.getY()) {
                    pFirst = point;
                    pFirstIndex = i;
                }
            }
        }
        return pFirstIndex;
    }

    public static TriangulatedMesh join(AdvancedTriangulatedMesh lower, AdvancedTriangulatedMesh upper) {

        //merging existing parts
        TriangulatedMesh mesh = new TriangulatedMesh();
        int shift = lower.points.size();

        mesh.points = lower.points;
        mesh.triangles = lower.triangles;
        for (Point3D p : upper.points) {
            Point3D p1 = new Point3D(p.getX(), p.getY(), p.getZ());
            mesh.points.add(p1);
        }
        for (Triangle t : upper.triangles) {
            Triangle tNew = new Triangle(t.getP1() + shift, t.getP2() + shift, t.getP3() + shift);
            mesh.triangles.add(tNew);

        }

        //connecting bounds
        //2 closest points
        double bestDist = 10000000;
        int bestP1 = -1;
        int bestP2 = -1;
        for (int p1 : lower.bound) {
            Point3D point1 = lower.points.get(p1);
            for (int p2 : upper.bound) {
                Point3D point2 = upper.points.get(p2);
                double d = point1.distance(point2);
                if (d < bestDist) {
                    bestP1 = p1;
                    bestP2 = p2;
                    bestDist = d;
                }
            }
        }
        //hack!!
        Collections.reverse(lower.bound);
        //shift to them
        int lStart = lower.bound.indexOf(bestP1);
        int uStart = upper.bound.indexOf(bestP2);

        Collections.rotate(upper.bound, -uStart);
        Collections.rotate(lower.bound, -lStart);

        //todo: check directions

        //merging
        int l = 0;
        int u = 0;
        List<Triangle> triangles = new ArrayList<>();
        while (l < lower.bound.size() || u < upper.bound.size()) {
            if (l == lower.bound.size()) {
                Triangle t = new Triangle(take(upper.bound, u) + shift, take(lower.bound, l), take(upper.bound, u + 1) + shift);
                triangles.add(t);
                u++;
                continue;
            }
            if (u == upper.bound.size()) {
                Triangle t = new Triangle(take(lower.bound, l), take(upper.bound, u) + shift, take(lower.bound, l + 1));
                triangles.add(t);
                l++;
                continue;
            }
            Point3D lp = lower.points.get(take(lower.bound, l));
            Point3D up = upper.points.get(take(upper.bound, u));
            Point3D lNext = lower.points.get(take(lower.bound, l + 1));
            Point3D uNext = upper.points.get(take(upper.bound, u + 1));

            boolean takeLower = up.distance(lNext) < lp.distance(uNext);
            if (takeLower) {
                Triangle t = new Triangle(take(lower.bound, l), take(upper.bound, u) + shift, take(lower.bound, l + 1));
                triangles.add(t);
                l++;
            } else {
                Triangle t = new Triangle(take(upper.bound, u) + shift, take(lower.bound, l), take(upper.bound, u + 1) + shift);
                triangles.add(t);
                u++;
            }
        }
        Constants.ADDED_TRIANGLES = triangles.size();
        mesh.triangles.addAll(triangles);
        return mesh;
    }

    public static TriangulatedMesh getPartialMesh(TriangulatedMesh source, double yThreshold, boolean above) {
        Map<Integer, Integer> allowedVertices = new HashMap<>();

        List<Point3D> points = source.points;
        List<Point3D> newPoints = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < points.size(); i++) {
            Point3D point = points.get(i);
            if (above) {
                if (point.getY() > yThreshold) {
                    newPoints.add(point);
                    allowedVertices.put(i, counter);
                    counter++;
                }
            } else {
                if (point.getY() < yThreshold) {
                    newPoints.add(point);
                    allowedVertices.put(i, counter);
                    counter++;
                }
            }
        }

        List<Triangle> triangles = source.triangles;
        List<Triangle> newTriangles = new ArrayList<>();
        for (Triangle t : triangles) {
            int p1 = t.getP1();
            int p2 = t.getP2();
            int p3 = t.getP3();
            if (allowedVertices.containsKey(p1)) {
                if (allowedVertices.containsKey(p2)) {
                    if (allowedVertices.containsKey(p3)) {
                        Triangle triangle = new Triangle(
                                allowedVertices.get(p1),
                                allowedVertices.get(p2),
                                allowedVertices.get(p3)
                        );
                        newTriangles.add(triangle);
                    }
                }
            }
        }

        TriangulatedMesh result = new TriangulatedMesh();
        result.points = newPoints;
        result.triangles = newTriangles;
        return result;
    }

    private static int take(List<Integer> list, int index) {
        if (index == list.size()) {
            return list.get(0);
        } else {
            return list.get(index);
        }
    }
}
