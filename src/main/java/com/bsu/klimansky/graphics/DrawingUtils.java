package com.bsu.klimansky.graphics;

import com.bsu.klimansky.model.Edge;
import com.bsu.klimansky.model.MeshCut;
import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class DrawingUtils {

    public static void drawMesh(TriangulatedMesh mesh, TestAnalysis ta, int offset) {
        List<Polygon> lp = new ArrayList<>();
        for(int i = 0; i < mesh.triangles.size() - offset; i++) {
            lp.add(createTriangle(mesh.triangles.get(i), mesh, Color.CYAN));
        }
        ta.draw(new Shape(lp));
    }

    public static void drawCut(TriangulatedMesh mesh, MeshCut cut, TestAnalysis ta) {
        List<Polygon> lp = new ArrayList<>();
        for(Edge e : cut.edges) {
            lp.add(createTriangle(Color.BLACK, mesh.points.get(e.p1), mesh.points.get(e.p2), mesh.points.get(e.p2)));
        }
        ta.draw(new Shape(lp));
    }

    public static void drawPointsOfMesh(List<Integer> points, TriangulatedMesh mesh, TestAnalysis ta) {
        Polygon polygon = new Polygon();
        polygon.setWireframeColor(Color.BLACK);

        for(Integer i : points) {
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
        return createTriangle(color, p1, p2, p3);
    }

    private static Polygon createTriangle(Color color, Point3D p1, Point3D p2, Point3D p3) {
        Polygon polygon = new Polygon();
        polygon.setWireframeColor(Color.BLACK);
        polygon.add(new Point(new Coord3d(p1.getX(), p1.getZ(), p1.getY())));
        polygon.add(new Point(new Coord3d(p2.getX(), p2.getZ(), p2.getY())));
        polygon.add(new Point(new Coord3d(p3.getX(), p3.getZ(), p3.getY())));
        polygon.setColor(color);
        return polygon;
    }


}
