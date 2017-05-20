package com.bsu.klimansky.model;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class MeshCut {
    private AdvancedTriangulatedMesh originalMesh;
    public double y;
    public List<Edge> edges;

    //todo:
    // points in order
    // center
    // time series

    public MeshCut(double y, AdvancedTriangulatedMesh advancedTriangulatedMesh) {
        this.originalMesh = advancedTriangulatedMesh;
        this.y = y;

        List<Integer> sortedPoints = advancedTriangulatedMesh.pointsByHeightAsc;
        List<Point3D> points = advancedTriangulatedMesh.points;
        edges = new ArrayList<>();
        for (int p : sortedPoints) {
            if (points.get(p).getY() > y) break;
            if (advancedTriangulatedMesh.downToUp.containsKey(p)) {
                List<Integer> neighbours = advancedTriangulatedMesh.downToUp.get(p);
                for (Integer next : neighbours) {
                    if (points.get(next).getY() > y) {
                        edges.add(new Edge(p, next));
                        //todo: add intersection point
                    }
                }
            }
        }

        //todo: build polygon by intersection points.
    }
}
