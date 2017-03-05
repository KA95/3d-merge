import javafx.geometry.Point3D;

import java.io.IOException;

/**
 * Created by Anton Klimansky on 05.03.2017.
 */
public class MeshJoiner {
    private static final String PATH_TO_LOWER_PART = Constants.DIRECTORY + "bunny10k_lower.json";
    private static final String PATH_TO_UPPER_PART = Constants.DIRECTORY + "bunny10k_upper.json";
    private static final String PATH_TO_OUT = Constants.DIRECTORY + "bunny10k_joined.ply";

//    private static final double SEPARATION_SIZE = 0.03;
    private static final double SEPARATION_SIZE = 0;

    public static void main(String[] args) throws IOException {
        Reader reader = new Reader();

        TriangulatedMesh lower = reader.readMeshFromJson(PATH_TO_LOWER_PART);
        TriangulatedMesh upper = reader.readMeshFromJson(PATH_TO_UPPER_PART);

        TriangulatedMesh result = join(lower, upper);

        Writer writer = new Writer();
        writer.write(result, PATH_TO_OUT);
        System.out.println("OK");
    }

    private static TriangulatedMesh join(TriangulatedMesh lower, TriangulatedMesh upper) {
        //todo: build bounds
        //todo: merge bounds
        TriangulatedMesh mesh = new TriangulatedMesh();
        int shift = lower.points.size();
        mesh.points = lower.points;
        mesh.triangles = lower.triangles;
        for(Point3D p : upper.points){
            //modify point
            Point3D p1 = new Point3D(p.getX(),p.getY() + SEPARATION_SIZE, p.getZ());
            mesh.points.add(p1);
        }
        for(Triangle t : upper.triangles) {
            Triangle tNew = new Triangle(t.getP1() + shift, t.getP2() + shift, t.getP3() + shift);
            mesh.triangles.add(tNew);

        }

        return mesh;
    }

}
