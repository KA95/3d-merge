import javafx.geometry.Point3D;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Anton Klimansky on 02.03.2017.
 */
public class Reader {

    private double Y_MIN = 10000;
    private double Y_MAX = -10000;

    public TriangulatedMesh readMeshFromJson(String filePath) {

        JSONParser parser = new JSONParser();

        List<Point3D> points = null;
        List<Triangle> triangles = null;
        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray vertexLists = (JSONArray) jsonObject.get("vertices");
            for (Object vertexList : vertexLists) {
                JSONObject jVertexList = (JSONObject) vertexList;
                if ("position_buffer".equals(jVertexList.get("name"))) {
                    JSONArray vertices = (JSONArray) jVertexList.get("values");
                    points = getPoints(vertices);
                }
            }

            JSONObject connectivity = (JSONObject) ((JSONArray) jsonObject.get("connectivity")).get(0);
            JSONArray indices = (JSONArray) connectivity.get("indices");
            triangles = getTriangles(indices);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TriangulatedMesh mesh = new TriangulatedMesh();
        mesh.points = points;
        mesh.triangles = triangles;
        return mesh;
    }

    public List<TriangulatedMesh> readMeshFromJsonSeparatedBy2(String filePath) {
        TriangulatedMesh mesh = readMeshFromJson(filePath);

        double thresholdLower = (Y_MAX - Y_MIN) * (0.5 - Constants.OVERLAPPING);
        double thresholdUpper = (Y_MAX - Y_MIN) * (0.5 + Constants.OVERLAPPING);

        return Arrays.asList(getPartialMesh(mesh, thresholdLower, true),
                getPartialMesh(mesh, thresholdUpper, false));
    }

    TriangulatedMesh getPartialMesh(TriangulatedMesh source, double yThreshold, boolean above) {
        Map<Integer, Integer> allowedVertices = new HashMap<>();

        List<Point3D> points = source.points;
        List<Point3D> newPoints = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < points.size(); i++) {
            Point3D point = points.get(i);
            if(above) {
                if(point.getY() > yThreshold) {
                    newPoints.add(point);
                    allowedVertices.put(i, counter);
                    counter++;
                }
            } else {
                if(point.getY() < yThreshold) {
                    newPoints.add(point);
                    allowedVertices.put(i, counter);
                    counter++;
                }
            }
        }

        List<Triangle> triangles = source.triangles;
        List<Triangle> newTriangles = new ArrayList<>();
        for(Triangle t : triangles) {
            int p1 = t.getP1();
            int p2 = t.getP2();
            int p3 = t.getP3();
            if(allowedVertices.containsKey(p1)) {
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

    private List<Point3D> getPoints(JSONArray vertices) {
        List<Point3D> points;
        points = new ArrayList<>();
        int pointsCount = vertices.size() / 3;
        for (int i = 0; i < pointsCount; i++) {

            Object xObj = vertices.get(3 * i);
            Object yObj = vertices.get(3 * i + 1);
            Object zObj = vertices.get(3 * i + 2);

            double x = Double.parseDouble(xObj.toString());
            double y = Double.parseDouble(yObj.toString());
            double z = Double.parseDouble(zObj.toString());
            Y_MIN = Math.min(Y_MIN, y);
            Y_MAX = Math.max(Y_MAX, y);
            points.add(new Point3D(x, y, z));
        }
        return points;
    }

    private List<Triangle> getTriangles(JSONArray indices) {
        List<Triangle> triangles;
        triangles = new ArrayList<>();
        int pointsCount = indices.size() / 3;
        for (int i = 0; i < pointsCount; i++) {
            int p1 = Integer.parseInt(indices.get(3 * i).toString());
            int p2 = Integer.parseInt(indices.get(3 * i + 1).toString());
            int p3 = Integer.parseInt(indices.get(3 * i + 2).toString());
            triangles.add(new Triangle(p1, p2, p3));
        }
        return triangles;
    }
}
