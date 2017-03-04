import java.io.IOException;
import java.util.List;

/**
 * Created by Anton Klimansky on 26.02.2017.
 */
public class Main {

    private static final String PATH_TO_FILE = "D:\\_diploma\\bunny10k.json";
    private static final String PATH_TO_OUT = "D:\\_diploma\\bunny10k_out.ply";
    private static final String PATH_TO_OUT_FORMAT = "D:\\_diploma\\bunny10k_out%d.ply";

    public static void main(String[] args) throws IOException {
        Reader reader = new Reader();
        Writer writer = new Writer();
//        TriangulatedMesh result = reader.readMeshFromJson(PATH_TO_FILE);
        List<TriangulatedMesh> result = reader.readMeshFromJsonSeparatedBy2(PATH_TO_FILE);

        for(int i = 0; i < result.size(); i++) {
            writer.write(result.get(i), String.format(PATH_TO_OUT_FORMAT, i));
        }
        System.out.println("Done!");
    }
}