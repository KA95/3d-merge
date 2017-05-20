package com.bsu.klimansky.model;

import com.bsu.klimansky.util.Constants;
import javafx.geometry.Point3D;

import java.util.*;

import static com.bsu.klimansky.util.TriangulatedMeshUtil.boundForMesh;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class AdvancedTriangulatedMesh extends TriangulatedMesh {

    public static int counter = 0;
    private double yMin;
    private double yMax;

    private double cutAreaTop;
    private double cutAreaBottom;
    private double step;

    public boolean upper;
    public List<Integer> bound;
    public List<MeshCut> cuts;

    public Map<Integer, List<Integer>> downToUp;
    public Map<Integer, List<Integer>> upToDown;

    public List<Integer> pointsByHeightAsc;

    public AdvancedTriangulatedMesh(TriangulatedMesh mesh, boolean upper) {
        this.upper = upper;
        triangles = mesh.triangles;
        points = mesh.points;
        bound = boundForMesh(mesh, upper);
        bound.remove(bound.size() - 1);

        //upper specific
        yMin = points.get(0).getY();
        yMax = yMin;

        for (Point3D p : points) {
            yMin = Math.min(yMin, p.getY());
            yMax = Math.max(yMax, p.getY());
        }

        boolean[][] used = new boolean[points.size()][points.size()];


        //Initialize edges
        upToDown = new HashMap<>();
        downToUp = new HashMap<>();
        for (Triangle tr : triangles) {
            addEdge(tr.getP1(), tr.getP2(), used);
            addEdge(tr.getP2(), tr.getP3(), used);
            addEdge(tr.getP1(), tr.getP3(), used);
        }

        //Sort points
        pointsByHeightAsc = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            pointsByHeightAsc.add(i);
        }
        pointsByHeightAsc.sort((p1, p2) -> (int) Math.signum(points.get(p1).getY() - points.get(p2).getY()));

        //Build cuts
        cutAreaTop = (yMax + yMin) / 2;
        cutAreaBottom = yMin;
        for (int i : bound) {
            cutAreaBottom = Math.max(cutAreaBottom, points.get(i).getY());
        }
        step = (cutAreaTop - cutAreaBottom) / Constants.FREQUENCY;
        cuts = new ArrayList<>();
        for (int i = 0; i <= Constants.FREQUENCY; i++) {
            cuts.add(new MeshCut(cutAreaBottom + i * step, this));
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
        System.out.println(counter++);
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
