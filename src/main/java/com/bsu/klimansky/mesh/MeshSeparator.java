package com.bsu.klimansky.mesh;

import com.bsu.klimansky.graphics.TestAnalysis;
import com.bsu.klimansky.util.Constants;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.io.Writer;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;

import java.util.*;

import static com.bsu.klimansky.graphics.DrawingUtils.*;

import static com.bsu.klimansky.util.TriangulatedMeshUtil.getPartialMesh;


/**
 * Created by Anton Klimansky on 26.02.2017.
 */
public class MeshSeparator {

//    private static final String PATH_TO_FILE = Constants.DIRECTORY + "Laurana50k.json";
    private static final String PATH_TO_FILE = Constants.DIRECTORY + "bunny10k.json";
//    private static final String PATH_TO_OUT_FORMAT = Constants.DIRECTORY + "Laurana50k_out%d.ply";

    public static void main(String[] args) throws Exception {
        Reader r = new Reader();
        TriangulatedMesh result = r.readMeshFromJson(PATH_TO_FILE);
        System.out.println(r.yMax - r.yMin);
//        Writer writer = new Writer();
//        for (int i = 0; i < result.size(); i++) {
//            writer.write(result.get(i), String.format(PATH_TO_OUT_FORMAT, i));
//        }
//        System.out.println("Done!");
    }

    private static List<TriangulatedMesh> readMeshFromJsonSeparatedBy2(String filePath) throws Exception {
        Reader reader = new Reader();
        TriangulatedMesh mesh = reader.readMeshFromJson(filePath);
        double thresholdLower = reader.yMin + (reader.yMax - reader.yMin) * (0.5 - Constants.OVERLAPPING);
        double thresholdUpper = reader.yMin + (reader.yMax - reader.yMin) * (0.5 + Constants.OVERLAPPING);

        TriangulatedMesh upper = getPartialMesh(mesh, thresholdLower, true);
        upper.moveVerticallyTo(reader.yMin);
        upper.rotate(Math.PI / 2);

        TriangulatedMesh lower = getPartialMesh(mesh, thresholdUpper, false);



        TestAnalysis ta = new TestAnalysis();
        AnalysisLauncher.open(ta);

        Point3D p = lower.points.get(0);
        ta.draw(createTriangle(Color.BLACK, p, p, p, Color.BLACK));
//
        drawMesh(lower, ta, 0);

        return Arrays.asList(upper, lower);
    }


}