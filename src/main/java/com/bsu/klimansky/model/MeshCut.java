package com.bsu.klimansky.model;

import com.bsu.klimansky.graphics.DrawingUtils;
import com.bsu.klimansky.graphics.TestAnalysis;
import com.bsu.klimansky.util.Constants;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Polygon;

import java.util.*;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class MeshCut {
    private AdvancedTriangulatedMesh originalMesh;
    public double y;

    /**
     * IMPORTANT: cutEdges[i] corresponds to cutPoints[i]
     * edge.p1 < edge.p2 (because of using 'downToUp' map)
     */
    public List<Edge> cutEdges;
    public List<Point3D> cutPoints = new ArrayList<>();

    public List<Edge> edges = new ArrayList<>();

    /**
     * series[i] corresponds to angle i*(2*pi/SERIES_LENGTH)
     * contains distances to edges
     */
    public List<List<Double>> series;
    public Point3D center;

    public MeshCut(double y, AdvancedTriangulatedMesh advancedTriangulatedMesh) throws Exception {
        this.originalMesh = advancedTriangulatedMesh;
        this.y = y;

        List<Integer> sortedPoints = advancedTriangulatedMesh.pointsByHeightAsc;
        List<Point3D> points = advancedTriangulatedMesh.points;
        cutEdges = new ArrayList<>();
        for (int p : sortedPoints) {
            if (points.get(p).getY() > y) break;
            if (advancedTriangulatedMesh.downToUp.containsKey(p)) {
                List<Integer> neighbours = advancedTriangulatedMesh.downToUp.get(p);
                for (Integer next : neighbours) {
                    if (points.get(next).getY() > y) {
                        cutEdges.add(new Edge(p, next));
                        Point3D p1 = points.get(p);
                        Point3D p2 = points.get(next);

                        double x = p1.getX() * (p2.getY() - y) + p2.getX() * (y - p1.getY());
                        x = x / (p2.getY() - p1.getY());//todo: what if equals?
                        double z = p1.getZ() * (p2.getY() - y) + p2.getZ() * (y - p1.getY());
                        z = z / (p2.getY() - p1.getY());//todo: what if equals?

                        cutPoints.add(new Point3D(x, y, z));
                    }
                }
            }
        }

        for (int i = 0; i < cutEdges.size() - 1; i++) {
            for (int j = i + 1; j < cutEdges.size(); j++) {
                Edge e1 = cutEdges.get(i);
                Edge e2 = cutEdges.get(j);
                ArrayList<Integer> p = new ArrayList<>(Arrays.asList(e1.p1, e1.p2));
                if (e1.p1 == e2.p1) {
                    p.add(e2.p2);
                } else if (e1.p1 == e2.p2) {
                    p.add(e2.p1);
                } else if (e1.p2 == e2.p1) {
                    p.add(e2.p2);
                } else if (e1.p2 == e2.p2) {
                    p.add(e2.p1);
                } else {
                    continue;
                }
                p.sort(Comparator.comparingInt(o -> o));
                if (originalMesh.pointsOfTriangles.containsKey(p.get(0))) {
                    if (originalMesh.pointsOfTriangles.get(p.get(0)).containsKey(p.get(1))) {
                        if (originalMesh.pointsOfTriangles.get(p.get(0)).get(p.get(1)).contains(p.get(2))) {
                            edges.add(new Edge(i, j));
                        }
                    }
                }
            }
        }
        double xc = 0;
        double yc = 0;
        double zc = 0;
        for (Point3D p : cutPoints) {
            xc += p.getX();
            yc += p.getY();
            zc += p.getZ();
        }
        xc /= cutPoints.size();
        yc /= cutPoints.size();
        zc /= cutPoints.size();

        center = new Point3D(xc, yc, zc);

        buildTimeSeries();

    }

    private void buildTimeSeries() throws Exception {

//        TestAnalysis ta = new TestAnalysis();
//        AnalysisLauncher.open(ta);
        //should be optimized
        series = new ArrayList<>();
        double step = Math.PI * 2 / Constants.SERIES_LENGTH;
        for (int i = 0; i < Constants.SERIES_LENGTH; i++) {

            series.add(new ArrayList<>());
            double angle = step * i;
            //-----------------
//            Point3D ap = new Point3D(center.getX() + Math.cos(angle) * 0.1, center.getY(), center.getZ() + Math.sin(angle) * 0.1);
//            Polygon anglep = DrawingUtils.createTriangle(Color.BLUE, center, ap, ap);
//            ta.draw(anglep);
//            Thread.sleep(200);
            //-----------------
            for (Edge e : edges) {
                Point3D p13d = cutPoints.get(e.p1);
                Point3D p23d = cutPoints.get(e.p2);

                Point2D p1 = new Point2D(p13d.getX() - center.getX(), p13d.getZ() - center.getZ());
                Point2D p2 = new Point2D(p23d.getX() - center.getX(), p23d.getZ() - center.getZ());


                Point2D O = new Point2D(0, 0);

                //conversion to polar
                double d1 = O.distance(p1);
                double a1 = getAngle(p1);

                double d2 = O.distance(p2);
                double a2 = getAngle(p2);

                if (a1 > a2) {
                    double buffer = a1;
                    a1 = a2;
                    a2 = buffer;
                }

                if (a1 < 0.5 * Math.PI && a2 > 1.5 * Math.PI) {
                    a2 -= 2 * Math.PI;
                    double buffer = a1;
                    a1 = a2;
                    a2 = buffer;
                }

                if (angle < a1 || angle > a2) continue;

                //visualize
//                Polygon ep = DrawingUtils.createTriangle(Color.RED, p13d, p23d, p23d);
//                ta.draw(ep);
//                Thread.sleep(100);

                //find normal point
                double A = p1.getY() - p2.getY();
                double B = p2.getX() - p1.getX();
                double C = p1.getX() * p2.getY() - p2.getX() * p1.getY();

                if (C > 0) {
                    A = -A;
                    B = -B;
                    C = -C;
                }

                //(A,B) - normal vector to line
                double an = getAngle(new Point2D(A, B));
                double dn = -C / Math.sqrt(A * A + B * B);

                double d = dn / Math.cos(angle - an);

//                Point3D p = new Point3D(center.getX() + d * Math.cos(angle), center.getY(), center.getZ() + d * Math.sin(angle));
//                DrawingUtils.drawPoint(p, ta, Color.GRAY);
//                Thread.sleep(300);
                series.get(i).add(d);

            }

        }
    }

    private double getAngle(Point2D p) {
        Point2D zeroDegreesVector = new Point2D(1, 0);
        double a = zeroDegreesVector.angle(p);
        if (p.getY() < 0) {
            a = 360 - a;
        }
        return Math.toRadians(a);
    }

}
