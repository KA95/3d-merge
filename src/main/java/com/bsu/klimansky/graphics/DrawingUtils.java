package com.bsu.klimansky.graphics;

import com.bsu.klimansky.model.Edge;
import com.bsu.klimansky.model.MeshCut;
import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;
import com.bsu.klimansky.util.Constants;
import javafx.geometry.Point3D;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class DrawingUtils {

    public static void drawMesh(TriangulatedMesh mesh, TestAnalysis ta, int offset) {
        List<Polygon> lp = new ArrayList<>();
        for (int i = 0; i < mesh.triangles.size() - offset; i++) {
            lp.add(createTriangle(mesh.triangles.get(i), mesh, Color.CYAN));
        }
        ta.draw(new Shape(lp));
    }

    public static void drawEdgesUsedInCut(TriangulatedMesh mesh, MeshCut cut, TestAnalysis ta) {
        List<Polygon> lp = new ArrayList<>();
        for (Edge e : cut.cutEdges) {
            lp.add(createTriangle(Color.BLACK, mesh.points.get(e.p1), mesh.points.get(e.p2), mesh.points.get(e.p2), Color.BLACK));
        }
        ta.draw(new Shape(lp));
    }

    public static void drawCutPoints(MeshCut cut, TestAnalysis ta) {
        Coord3d[] newPoints = new Coord3d[cut.cutPoints.size()];
        for (int i = 0; i < cut.cutPoints.size(); i++) {
            Point3D p = cut.cutPoints.get(i);
            newPoints[i] = new Coord3d(p.getX(), p.getZ(), p.getY());
        }

        Scatter pointsScatter = new Scatter(newPoints, Color.RED, 2f);
        ta.draw(pointsScatter);
        Coord3d[] c = {new Coord3d(cut.center.getX(), cut.center.getZ(), cut.center.getY())};
        Scatter centerScatter = new Scatter(c, Color.BLACK, 4f);
        ta.draw(centerScatter);
    }

    public static void drawPoint(Point3D p, TestAnalysis ta, Color color) {
        Coord3d[] c = {new Coord3d(p.getX(), p.getZ(), p.getY())};
        Scatter centerScatter = new Scatter(c, color, 4f);
        ta.draw(centerScatter);
    }

    public static void drawTimeSeries(MeshCut cut, TestAnalysis ta) {
        List<Coord3d> newPoints = new ArrayList<>();
        double step = Math.PI * 2 / Constants.SERIES_LENGTH;
        for (int i = 0; i < cut.series.size(); i++) {
            List<Double> l = cut.series.get(i);
            for (Double d : l) {
                double x = cut.center.getX() + d * Math.cos(step * i);
                double y = cut.center.getZ() + d * Math.sin(step * i);

//                if (Math.abs(x) < 0.1 && Math.abs(y) < 0.1) {
                if (Math.abs(x) < 0.1 && Math.abs(y) < 0.1) {
//                if (Math.abs(x) < 1 && Math.abs(y) < 1) {
                    newPoints.add(new Coord3d(x, y, cut.y));
                }
            }
        }

        Coord3d[] coord3ds = new Coord3d[newPoints.size()];
        newPoints.toArray(coord3ds);
        Scatter pointsScatter = new Scatter(coord3ds, Color.BLUE, 2f);
        ta.draw(pointsScatter);
    }

    public static void drawCutEdges(MeshCut cut, TestAnalysis ta) {
        List<Polygon> lp = new ArrayList<>();
        for (Edge e : cut.edges) {
            lp.add(createTriangle(Color.BLACK, cut.cutPoints.get(e.p1), cut.cutPoints.get(e.p2), cut.cutPoints.get(e.p2), Color.BLACK));
        }
        ta.draw(new Shape(lp));
    }

    public static void drawPointsOfMesh(List<Integer> points, TriangulatedMesh mesh, TestAnalysis ta) {
        Polygon polygon = new Polygon();
        polygon.setWireframeColor(Color.BLACK);

        for (Integer i : points) {
            Point3D p = mesh.points.get(i);
            polygon.add(new Point(new Coord3d(p.getX(), p.getZ(), p.getY())));
            ta.draw(polygon);
        }
    }

    public static void drawTriangle(Triangle tr, TriangulatedMesh mesh, TestAnalysis ta) {
        Polygon polygon = createTriangle(tr, mesh, Color.RED);
        ta.draw(polygon);
    }

    private static Polygon createTriangle(Triangle tr, TriangulatedMesh mesh, Color color) {
        Point3D p1 = mesh.points.get(tr.getP1());
        Point3D p2 = mesh.points.get(tr.getP2());
        Point3D p3 = mesh.points.get(tr.getP3());
        return createTriangle(color, p1, p2, p3, Color.BLACK);
    }

    public static Polygon createTriangle(Color color, Point3D p1, Point3D p2, Point3D p3, Color wColor) {
        Polygon polygon = new Polygon();
        polygon.setWireframeColor(wColor);
        polygon.add(new Point(new Coord3d(p1.getX(), p1.getZ(), p1.getY())));
        polygon.add(new Point(new Coord3d(p2.getX(), p2.getZ(), p2.getY())));
        polygon.add(new Point(new Coord3d(p3.getX(), p3.getZ(), p3.getY())));
        polygon.setColor(color);
        return polygon;
    }


}
