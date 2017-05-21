package com.bsu.klimansky.model;

import com.bsu.klimansky.util.Constants;
import javafx.geometry.Point3D;

import java.util.*;

import static com.bsu.klimansky.util.TriangulatedMeshUtil.boundForMesh;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class AdvancedTriangulatedMesh extends TriangulatedMesh {


    public boolean upper;
    public List<Integer> bound;

    private double yMin;
    private double yMax;

    public Map<Integer, List<Integer>> downToUp;
    public Map<Integer, List<Integer>> upToDown;

    private double cutAreaTop;
    private double cutAreaBottom;
    private double step;


    public List<Integer> pointsByHeightAsc;

    public List<MeshCut> cuts;

    /**
     * Structure storing all triangles in form a->b->c where a<b<c
     **/
    public Map<Integer, Map<Integer, List<Integer>>> pointsOfTriangles = new HashMap<>();

    public AdvancedTriangulatedMesh(TriangulatedMesh mesh, boolean upper) throws Exception {
        this.upper = upper;
        triangles = mesh.triangles;
        points = mesh.points;
        bound = boundForMesh(mesh, upper);
        bound.remove(bound.size() - 1);
    }

    public void recalculate() throws Exception {
        yMin = points.get(0).getY();
        yMax = yMin;

        for (Point3D p : points) {
            yMin = Math.min(yMin, p.getY());
            yMax = Math.max(yMax, p.getY());
        }

        boolean[][] used = new boolean[points.size()][points.size()];


        //Initialize cutEdges
        upToDown = new HashMap<>();
        downToUp = new HashMap<>();
        for (Triangle tr : triangles) {
            addEdge(tr.getP1(), tr.getP2(), used);
            addEdge(tr.getP2(), tr.getP3(), used);
            addEdge(tr.getP1(), tr.getP3(), used);
            putTriangleToMap(tr);
        }

        //Sort points
        pointsByHeightAsc = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            pointsByHeightAsc.add(i);
        }
        pointsByHeightAsc.sort((p1, p2) -> (int) Math.signum(points.get(p1).getY() - points.get(p2).getY()));

        //Build cuts
        if (upper) {
            cutAreaTop = (yMax + yMin) / 2;
            cutAreaBottom = yMin;
            for (int i : bound) {
                //highest point of lower bound
                cutAreaBottom = Math.max(cutAreaBottom, points.get(i).getY());
            }
        } else {
            cutAreaTop = yMax;
//                cutAreaBottom = (yMax + yMin) / 2;
            cutAreaBottom = yMin;
            for (int i : bound) {
                //lowest point of higher bound
                cutAreaTop = Math.min(cutAreaTop, points.get(i).getY());
            }
        }

        step = (cutAreaTop - cutAreaBottom) / Constants.FREQUENCY;
        cuts = new ArrayList<>();
        for (int i = 0; i <= Constants.FREQUENCY; i++) {
            cuts.add(new MeshCut(cutAreaBottom + i * step, this));
        }

    }

    private void putTriangleToMap(Triangle tr) {
        List<Integer> list = new ArrayList<>(Arrays.asList(tr.getP1(), tr.getP2(), tr.getP3()));
        list.sort(Comparator.comparingInt(o -> o));
        int p1 = list.get(0);
        int p2 = list.get(1);
        int p3 = list.get(2);
        if (pointsOfTriangles.containsKey(p1)) {
            if (pointsOfTriangles.get(p1).containsKey(p2)) {
                pointsOfTriangles.get(p1).get(p2).add(p3);
            } else {
                ArrayList<Integer> lp3 = new ArrayList<>(Collections.singletonList(p3));
                pointsOfTriangles.get(p1).put(p2, lp3);
            }
        } else {
            ArrayList<Integer> lp3 = new ArrayList<>(Collections.singletonList(p3));
            Map<Integer, List<Integer>> p2p3 = new HashMap<>();
            p2p3.put(p2, lp3);
            pointsOfTriangles.put(p1, p2p3);
        }
    }

    private void addEdge(int p1, int p2, boolean[][] used) {
        if (used[p1][p2]) return;

        used[p1][p2] = true;
        used[p2][p1] = true;

        int upper = p1;
        int lower = p2;


        if (points.get(p1).getY() < points.get(p2).getY()) {
            upper = p2;
            lower = p1;
        }

        add(upper, lower, upToDown);
        add(lower, upper, downToUp);
    }

    private void add(int from, int to, Map<Integer, List<Integer>> edgesMap) {
        if (edgesMap.containsKey(from)) {
            edgesMap.get(from).add(to);
        } else {
            ArrayList<Integer> a = new ArrayList<>();
            a.add(to);
            edgesMap.put(from, a);
        }
    }
}
