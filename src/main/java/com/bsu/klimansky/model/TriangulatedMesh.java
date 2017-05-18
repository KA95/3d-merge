package com.bsu.klimansky.model;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Klimansky on 02.03.2017.
 */
public class TriangulatedMesh {
    public List<Point3D> points;
    public List<Triangle> triangles;

    public void moveVerticallyTo(double yLower) {
        double lower = points.get(0).getY();
        for (Point3D point3D : points) {
            lower = Math.min(lower, point3D.getY());
        }
        double delta = lower - yLower;
        List<Point3D> newPoints = new ArrayList<>();
        for (Point3D point3D : points) {
            newPoints.add(point3D.subtract(0, delta, 0));
        }
        points = newPoints;
    }

    public void rotate(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        List<Point3D> newPoints = new ArrayList<>();
        for (Point3D point3D : points) {
            double x = point3D.getX(), y = point3D.getY(), z = point3D.getZ();
            newPoints.add(new Point3D(c * x - s * z, y, s * x + c * z));
        }
        points = newPoints;
    }
}
