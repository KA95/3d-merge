package com.bsu.klimansky.mesh;

import com.bsu.klimansky.model.Triangle;
import com.bsu.klimansky.util.Constants;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.io.Writer;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;

import java.io.IOException;
import java.util.*;

import static com.bsu.klimansky.util.TriangulatedMeshUtil.getPartialMesh;


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


}