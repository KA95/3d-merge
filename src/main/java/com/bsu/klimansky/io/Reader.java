package com.bsu.klimansky.io;

import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Klimansky on 02.03.2017.
 */
public class Reader {

    public double yMin = 10000;
    public double yMax = -10000;

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
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        TriangulatedMesh mesh = new TriangulatedMesh();
        mesh.points = points;
        mesh.triangles = triangles;
        return mesh;
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
            yMin = Math.min(yMin, y);
            yMax = Math.max(yMax, y);
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
