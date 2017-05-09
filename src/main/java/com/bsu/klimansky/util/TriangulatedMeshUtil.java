package com.bsu.klimansky.util;

import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;

import java.util.*;

import static com.bsu.klimansky.util.GraphUtil.buildGraphByMesh;

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
        List<Point3D> points = mesh.points;

        // build graph
        Map<Integer, Set<Integer>> graph = buildGraphByMesh(mesh);
        // select first point
        int pFirstIndex = 0;
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
        return buildBound(graph, points, pFirstIndex, upper);
    }

    public static TriangulatedMesh join(TriangulatedMesh lower, TriangulatedMesh upper, List<Integer> lBound, List<Integer> uBound) {

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
        for (int p1 : lBound) {
            Point3D point1 = lower.points.get(p1);
            for (int p2 : uBound) {
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
//        Collections.reverse(lBound);
        //shift to them
        int lStart = lBound.indexOf(bestP1);
        int uStart = uBound.indexOf(bestP2);

        Collections.rotate(uBound, -uStart);
        Collections.rotate(lBound, -lStart);

        //todo: check directions

        //merging
        int l = 0;
        int u = 0;
        while (l < lBound.size() || u < uBound.size()) {
            if (l == lBound.size()) {
                Triangle t = new Triangle(take(uBound, u) + shift, take(lBound, l), take(uBound, u + 1) + shift);
                mesh.triangles.add(t);
                u++;
                continue;
            }
            if (u == uBound.size()) {
                Triangle t = new Triangle(take(lBound, l), take(uBound, u) + shift, take(lBound, l + 1));
                mesh.triangles.add(t);
                l++;
                continue;
            }
            Point3D lp = lower.points.get(take(lBound, l));
            Point3D up = upper.points.get(take(uBound, u));
            Point3D lNext = lower.points.get(take(lBound, l + 1));
            Point3D uNext = upper.points.get(take(uBound, u + 1));

            boolean takeLower = up.distance(lNext) < lp.distance(uNext);
            if (takeLower) {
                Triangle t = new Triangle(take(lBound, l), take(uBound, u) + shift, take(lBound, l + 1));
                mesh.triangles.add(t);
                l++;
            } else {
                Triangle t = new Triangle(take(uBound, u) + shift, take(lBound, l), take(uBound, u + 1) + shift);
                mesh.triangles.add(t);
                u++;
            }
        }

        return mesh;
    }

    private static List<Integer> buildBound(Map<Integer, Set<Integer>> graph, List<Point3D> points, int first, boolean upper) {
        List<Integer> result = new ArrayList<>();
        int current = first;
        int prev = -1;
        boolean[] used = new boolean[points.size()];
        for (int i = 0; i < points.size(); i++)
            used[i] = false;

        do {
            used[current] = true;
            result.add(current);
            int next = -1;
            Set<Integer> set = graph.get(current);
            Point3D pCur = points.get(current);
            Point3D pNext = null;
            for (int v : set) {
                if (v == first && first != prev) {
                    result.add(first);
                    break;
                }
                if (used[v]) continue;
                if (set.size() != 2 && result.size() > 2 && graph.get(v).contains(prev))
                    continue;
                if (next == -1) {
                    next = v;
                    pNext = points.get(v);
                    continue;
                }
                Point3D p = points.get(v);
                if (isBetter(p, pNext, pCur, upper)) {
                    next = v;
                    pNext = p;
                }
            }
            prev = current;
            current = next;
        } while (current != -1);

        return result;
    }

    private static int take(List<Integer> list, int index) {
        if (index == list.size()) {
            return list.get(0);
        } else {
            return list.get(index);
        }
    }

    private static boolean isBetter(Point3D p, Point3D best, Point3D current, boolean upper) {
        Point3D dp = new Point3D(p.getX() - current.getX(), p.getY() - current.getY(), p.getZ() - current.getZ());
        Point3D dbest = new Point3D(best.getX() - current.getX(), best.getY() - current.getY(), best.getZ() - current.getZ());
        if (upper) {
            return goalFunction(dp) < goalFunction(dbest);
        } else {
            return goalFunction(dp) > goalFunction(dbest);
        }
    }

    private static double goalFunction(Point3D point) {
//        return point.getY() / Math.sqrt(point.getX() * point.getX() + point.getZ() * point.getZ());
        return point.getY();
    }

}
