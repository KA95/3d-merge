package com.bsu.klimansky.mesh;

import com.bsu.klimansky.model.AdvancedTriangulatedMesh;
import com.bsu.klimansky.model.MeshCut;
import com.bsu.klimansky.util.Constants;
import com.bsu.klimansky.graphics.TestAnalysis;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.model.TriangulatedMesh;
import org.jzy3d.analysis.AnalysisLauncher;

import static com.bsu.klimansky.graphics.DrawingUtils.drawCut;
import static com.bsu.klimansky.util.TriangulatedMeshUtil.join;

/**
 * Created by Anton Klimansky on 05.03.2017.
 */
public class MeshJoiner {
//    private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "bunny10k_lower-simple.json";
//    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "bunny10k_upper-simple.json";

    private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "bunny10k_lower-complex.json";
    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "bunny10k_upper-complex.json";

    private static final String PATH_TO_OUT = Constants.DIRECTORY + "bunny10k_joined.ply";

    public static void main(String[] args) throws Exception {
        Reader reader = new Reader();

        //basic mesh available to vizalization
        TriangulatedMesh lower = reader.readMeshFromJson(PATH_TO_LOWER_PART);
        TriangulatedMesh upper = reader.readMeshFromJson(PATH_TO_UPPER_PART);


        AdvancedTriangulatedMesh aUpper = new AdvancedTriangulatedMesh(upper, true);
        AdvancedTriangulatedMesh aLower = new AdvancedTriangulatedMesh(lower, false);


        //TODO: main part
        //find matching and move Upper part properly


//        merging bounds
        TriangulatedMesh result = join(aLower, aUpper);

//        Writer writer = new Writer();
//        writer.write(result, PATH_TO_OUT);

//------------------------------------------------------------------------------------

        TestAnalysis ta = new TestAnalysis();
        AnalysisLauncher.open(ta);

//--------CUTS
        for(MeshCut cut : aUpper.cuts) {
            drawCut(aUpper, cut, ta);
            Thread.sleep(200);
        }

//--------MERGING(SIMPLE)!!!!!!

//        int offset = 127;
//        drawMesh(result, ta, offset);
//        for(int i = result.triangles.size() - offset; i < result.triangles.size(); i++) {
//            drawTriangle(result.triangles.get(i), result, ta);
//        }


//        drawMesh(aLower, ta, 0);
//        drawMesh(aUpper, ta, 0);


        System.out.println("Done");

    }

}
