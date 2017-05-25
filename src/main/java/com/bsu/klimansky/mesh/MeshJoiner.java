package com.bsu.klimansky.mesh;

import com.bsu.klimansky.graphics.TestAnalysis;
import com.bsu.klimansky.io.Writer;
import com.bsu.klimansky.model.AdvancedTriangulatedMesh;
import com.bsu.klimansky.util.Constants;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.model.TriangulatedMesh;
import javafx.geometry.Point3D;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;

import static com.bsu.klimansky.graphics.DrawingUtils.*;
import static com.bsu.klimansky.util.TriangulatedMeshUtil.join;

/**
 * Created by Anton Klimansky on 05.03.2017.
 */
public class MeshJoiner {
    private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "bunny10k_lower-simple.json";
    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "bunny10k_upper-simple.json";

    private static final String PATH_TO_OUT = Constants.DIRECTORY + "bunny10k_joined.ply";

    public static void main(String[] args) throws Exception {
        Reader reader = new Reader();

        //basic mesh available to vizalization
        TriangulatedMesh lower = reader.readMeshFromJson(PATH_TO_LOWER_PART);
        TriangulatedMesh upper = reader.readMeshFromJson(PATH_TO_UPPER_PART);

        AdvancedTriangulatedMesh aUpper = new AdvancedTriangulatedMesh(upper, true);
        AdvancedTriangulatedMesh aLower = new AdvancedTriangulatedMesh(lower, false);

        TriangulatedMesh result = join(aLower, aUpper);

        TestAnalysis ta = new TestAnalysis();
        AnalysisLauncher.open(ta);
        ta.draw(createTriangle(Color.BLACK, new Point3D(0, 0, 0), new Point3D(0, 0, 0), new Point3D(0, 0.0, 0), Color.BLACK));

        drawMesh(result, ta, 0);

        System.out.println("Done");

    }


}
