package com.bsu.klimansky.mesh;

import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.util.Constants;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.io.Writer;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;

import java.io.IOException;
import java.util.*;


/**
 * Created by Anton Klimansky on 26.02.2017.
 */
public class MeshSeparator {

    private static final String PATH_TO_FILE = Constants.DIRECTORY + "bunny10k.json";
    private static final String PATH_TO_OUT_FORMAT = Constants.DIRECTORY + "bunny10k_out%d.ply";

    public static void main(String[] args) throws IOException {
        List<TriangulatedMesh> result = readMeshFromJsonSeparatedBy2(PATH_TO_FILE);
        Writer writer = new Writer();
        for (int i = 0; i < result.size(); i++) {
            writer.write(result.get(i), String.format(PATH_TO_OUT_FORMAT, i));
        }
        System.out.println("Done!");
    }

    private static List<TriangulatedMesh> readMeshFromJsonSeparatedBy2(String filePath) {
        Reader reader = new Reader();
        TriangulatedMesh mesh = reader.readMeshFromJson(filePath);

        double thresholdLower = (reader.yMax - reader.yMin) * (0.5 - Constants.OVERLAPPING);
        double thresholdUpper = (reader.yMax - reader.yMin) * (0.5 + Constants.OVERLAPPING);

        TriangulatedMesh upper = getPartialMesh(mesh, thresholdLower, true);
        upper.moveVerticallyTo(reader.yMin);
        upper.rotate(Math.PI / 2);

        TriangulatedMesh lower = getPartialMesh(mesh, thresholdUpper, false);

        return Arrays.asList(upper, lower);
    }

    private static TriangulatedMesh getPartialMesh(TriangulatedMesh source, double yThreshold, boolean above) {
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


}