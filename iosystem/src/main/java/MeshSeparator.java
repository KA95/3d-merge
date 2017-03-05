import java.io.IOException;
import java.util.List;

/**
 * Created by Anton Klimansky on 26.02.2017.
 */
public class MeshSeparator {

    private static final String PATH_TO_FILE = Constants.DIRECTORY + "bunny10k.json";
    private static final String PATH_TO_OUT_FORMAT = Constants.DIRECTORY +"bunny10k_out%d.ply";

    public static void main(String[] args) throws IOException {

        Reader reader = new Reader();
        List<TriangulatedMesh> result = reader.readMeshFromJsonSeparatedBy2(PATH_TO_FILE);


        Writer writer = new Writer();
        for(int i = 0; i < result.size(); i++) {
            writer.write(result.get(i), String.format(PATH_TO_OUT_FORMAT, i));
        }

        System.out.println("Done!");
    }
}