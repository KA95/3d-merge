package com.bsu.klimansky.mesh;

import com.bsu.klimansky.util.Constants;
import com.bsu.klimansky.graphics.TestAnalysis;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;

import java.util.*;

import static com.bsu.klimansky.util.TriangulatedMeshUtil.boundForMesh;
import static com.bsu.klimansky.util.TriangulatedMeshUtil.join;

/**
 * Created by Anton Klimansky on 05.03.2017.
 */
public class MeshJoiner {
    private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "bunny10k_lower.json";
    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "bunny10k_upper.json";
    private static final String PATH_TO_OUT = Constants.DIRECTORY + "bunny10k_joined.ply";

    public static void main(String[] args) throws Exception {
        Reader reader = new Reader();

        TriangulatedMesh lower = reader.readMeshFromJson(PATH_TO_LOWER_PART);
        TriangulatedMesh upper = reader.readMeshFromJson(PATH_TO_UPPER_PART);

//        building bounds

        //numbers of points of bounds
        List<Integer> uBound = boundForMesh(upper, true);
        List<Integer> lBound = boundForMesh(lower, false);

        uBound.remove(uBound.size() - 1);
        lBound.remove(lBound.size() - 1);

//        merging bounds
        TriangulatedMesh result = join(lower, upper, lBound, uBound);

//        Writer writer = new Writer();
//        writer.write(result, PATH_TO_OUT);

//------------------------------------------------------------------------------------

        TestAnalysis ta = new TestAnalysis();
        AnalysisLauncher.open(ta);

        int numberOfBoundTriangles = 127;

        int end = result.triangles.size() - numberOfBoundTriangles;
        int start = 0;

        List<Polygon> lp = new ArrayList<>();
        for(int i = start; i < end; i++) {
            lp.add(createTriangle(result.triangles.get(i), result, Color.CYAN));
        }
        ta.draw(new Shape(lp));

        for(int i = end; i < result.triangles.size(); i++) {
            drawTriangle(result.triangles.get(i), result, ta);
        }

    }

    private static void drawPointsOfMesh(List<Integer> points, TriangulatedMesh mesh, TestAnalysis ta) {
        Polygon polygon = new Polygon();
        polygon.setWireframeColor(Color.BLACK);

        for(Integer i : points) {
            Point3D p = mesh.points.get(i);
            polygon.add(new Point(new Coord3d(p.getX(), p.getZ(), p.getY())));
            ta.draw(polygon);
        }
    }

    private static void drawTriangle(Triangle tr, TriangulatedMesh mesh, TestAnalysis ta) {
        Polygon polygon = createTriangle(tr, mesh, Color.RED);
        ta.draw(polygon);
    }

    private static Polygon createTriangle(Triangle tr, TriangulatedMesh mesh, Color color) {
        Polygon polygon = new Polygon();
        polygon.setWireframeColor(Color.BLACK);
        Point3D p1 = mesh.points.get(tr.getP1());
        Point3D p2 = mesh.points.get(tr.getP2());
        Point3D p3 = mesh.points.get(tr.getP3());
        polygon.add(new Point(new Coord3d(p1.getX(), p1.getZ(), p1.getY())));
        polygon.add(new Point(new Coord3d(p2.getX(), p2.getZ(), p2.getY())));
        polygon.add(new Point(new Coord3d(p3.getX(), p3.getZ(), p3.getY())));
        polygon.setColor(color);
        return polygon;
    }

}
