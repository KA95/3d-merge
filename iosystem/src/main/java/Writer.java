import javafx.geometry.Point3D;

import java.io.*;
import java.util.Locale;

/**
 * Created by Anton Klimansky on 02.03.2017.
 */
public class Writer {
    private static final String HEADER_FORMAT = "ply\n" +
            "format ascii 1.0\n" +
            "element vertex %d\n" +
            "property float x\n" +
            "property float y\n" +
            "property float z\n" +
            "element face %d\n" +
            "property list uchar int vertex_index\n" +
            "end_header\n";

    public void write(TriangulatedMesh mesh, String outputFile) throws IOException {
        Locale.setDefault(Locale.ENGLISH);
        FileOutputStream fos = new FileOutputStream(outputFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        try {
            osw.write(String.format(HEADER_FORMAT, mesh.points.size(), mesh.triangles.size()));
            for (Point3D p : mesh.points) {
                osw.write(String.format("%f %f %f\n", p.getX(), p.getY(), p.getZ()));
            }
            for (Triangle t : mesh.triangles) {
                osw.write(String.format("3 %d %d %d\n", t.getP1(), t.getP2(), t.getP3()));
            }
        } finally {
            osw.close();
            fos.close();
        }
    }
}
