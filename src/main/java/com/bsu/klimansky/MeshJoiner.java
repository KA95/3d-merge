package com.bsu.klimansky;

import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.io.Writer;
import com.bsu.klimansky.model.TriangulatedMesh;

import java.io.IOException;
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

    public static void main(String[] args) throws IOException {
        Reader reader = new Reader();

        TriangulatedMesh lower = reader.readMeshFromJson(PATH_TO_LOWER_PART);
        TriangulatedMesh upper = reader.readMeshFromJson(PATH_TO_UPPER_PART);

//        building bounds

        List<Integer> uBound = boundForMesh(upper, true);
        List<Integer> lBound = boundForMesh(lower, false);

        uBound.remove(uBound.size() - 1);
        lBound.remove(lBound.size() - 1);

//        merging bounds
        TriangulatedMesh result = join(lower, upper, lBound, uBound);
        Writer writer = new Writer();
        writer.write(result, PATH_TO_OUT);

        System.out.println("OK");
    }

}
