package com.bsu.klimansky.mesh;

import com.bsu.klimansky.graphics.TestAnalysis;
import com.bsu.klimansky.io.Reader;
import com.bsu.klimansky.io.Writer;
import com.bsu.klimansky.model.AdvancedTriangulatedMesh;
import com.bsu.klimansky.model.MeshCut;
import com.bsu.klimansky.model.TriangulatedMesh;
import com.bsu.klimansky.util.Constants;
import javafx.geometry.Point3D;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;

import java.util.List;

import static com.bsu.klimansky.graphics.DrawingUtils.*;
import static com.bsu.klimansky.util.TriangulatedMeshUtil.join;
import static com.bsu.klimansky.util.TriangulatedMeshUtil.visualizeBound;

/**
 * Created by Anton Klimansky on 5/22/2017.
 */
public class MeshMacther {

    private static long timerStart;
    private static String timerName;
    private static long total = 0;


//    private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "bunny10k_lower-complex.json";
//    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "bunny10k_upper-complex.json";
        private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "Laurana50k_lower.json";
    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "Laurana50k_upper.json";
    //    private static final String PATH_TO_LOWER_OUT = Constants.DIRECTORY + "bunny10k_lower-matched.ply";
//    private static final String PATH_TO_UPPER_OUT = Constants.DIRECTORY + "bunny10k_upper-matched.ply";
//    private static final String PATH_TO_OUT = Constants.DIRECTORY + "bunny10k%d-%d_joined.ply";
    private static final String PATH_TO_OUT = Constants.DIRECTORY + "Laurana50k%d-%d_joined.ply";


    public static void main(String[] args) throws Exception {
        Reader reader = new Reader();

        start("Reading");
        TriangulatedMesh lower = reader.readMeshFromJson(PATH_TO_LOWER_PART);
        TriangulatedMesh upper = reader.readMeshFromJson(PATH_TO_UPPER_PART);
        end();
//---------------------------------------------------------------------
        start("Initialize meshes");
        AdvancedTriangulatedMesh aUpper = new AdvancedTriangulatedMesh(upper, true);
        aUpper.recalculate();
        AdvancedTriangulatedMesh aLower = new AdvancedTriangulatedMesh(lower, false);
        aLower.recalculate();
        end();
//---------------------------------------------------------------------
        start("Matching");
        Solution[][] solutions = new Solution[Constants.FREQUENCY + 1][Constants.FREQUENCY + 1];
        for (int i = 0; i <= Constants.FREQUENCY; i++) {
            for (int j = 0; j <= Constants.FREQUENCY; j++) {
                solutions[i][j] = match(aLower.cuts.get(i), aUpper.cuts.get(j));
            }
        }
        Solution best = solutions[0][0];
        int cutL = 0;
        int cutU = 0;
        for (int i = 0; i <= Constants.FREQUENCY; i++) {
            for (int j = 0; j <= Constants.FREQUENCY; j++) {
                if (best.distance > solutions[i][j].distance) {
                    best = solutions[i][j];
                    cutL = i;
                    cutU = j;
                }
            }
        }
        end();
//---------------------------------------------------------------------
        start("Other");
        MeshCut upperCut = aUpper.cuts.get(cutU);
        MeshCut lowerCut = aLower.cuts.get(cutL);

        Point3D translation = lowerCut.center.subtract(upperCut.center);
        aUpper.move(translation);
        aUpper.rotate(-best.angle, lowerCut.center);

        double separatorY = lowerCut.center.getY();

        TriangulatedMesh rLower = aLower.getTruncated(separatorY, false);
        TriangulatedMesh rUpper = aUpper.getTruncated(separatorY, true);

        AdvancedTriangulatedMesh lower1 = new AdvancedTriangulatedMesh(rLower, false);
        AdvancedTriangulatedMesh upper1 = new AdvancedTriangulatedMesh(rUpper, true);

        TriangulatedMesh result = join(lower1, upper1);
        end();
//---------------------------------------------------------------------
        Writer writer = new Writer();
        writer.write(result, String.format(PATH_TO_OUT, Constants.FREQUENCY, Constants.SERIES_LENGTH));

        System.out.println("angle error: " + Math.abs((best.angle - Constants.ORIGINAL_ANGLE) / Constants.ORIGINAL_ANGLE * 100));
        System.out.println("height error: " + Math.abs((result.calculateHeight() - Constants.LAURANA_HEIGHT) / Constants.LAURANA_HEIGHT * 100));

        //best for calculation;
        System.out.println("Total : " + total + " ms");
        System.out.println("Done");

    }

    private static Solution match(MeshCut mc1, MeshCut mc2) {
        double bestAngle = 0;
        double bestDistance = 1000000000;
        double step = 2 * Math.PI / Constants.SERIES_LENGTH;
        for (int shift = 0; shift < Constants.SERIES_LENGTH; shift++) {
            double dist = 0;
            for (int j = 0; j < Constants.SERIES_LENGTH; j++) {
                dist += distance(mc1.series.get(j), mc2.series.get((j + shift) % Constants.SERIES_LENGTH));
            }
            if (dist < bestDistance) {
                bestDistance = dist;
                bestAngle = step * shift;
            }
        }
        Solution solution = new Solution(bestDistance, bestAngle);
        return solution;
    }

    private static double distance(List<Double> d1, List<Double> d2) {
        if (d1.size() == 0 && d2.size() == 0) {
            return 0;
        }
        double result = 0;
        for (Double d : d1) {
            result += d;
        }
        for (Double d : d2) {
            result -= d;
        }
        return Math.abs(result);
    }

    private static class Solution {
        public double angle;
        public double distance;

        public Solution(double bestDistance, double bestAngle) {
            angle = bestAngle;
            distance = bestDistance;
        }
    }

    public static void start(String name) {
        timerName = name;
        timerStart = System.currentTimeMillis();
    }

    public static void end() {
        long end = System.currentTimeMillis();
        System.out.println(timerName + " : " + (end - timerStart) + " ms");
        total += end - timerStart;
        timerStart = 0;
        timerName = "";
    }

}
