import java.io.IOException;

/**
 * Created by Anton Klimansky on 26.02.2017.
 */
public class Main {

    private static final String PATH_TO_FILE = "D:\\BSU\\diplom\\bunny2.json";
    private static final String PATH_TO_OUT = "D:\\BSU\\diplom\\bunny2_out.ply";

    public static void main(String[] args) throws IOException {
        Reader reader = new Reader();
        Writer writer = new Writer();
        TriangulatedMesh result = reader.readMeshFromJson(PATH_TO_FILE);
        writer.write(result, PATH_TO_OUT);
        System.out.println("Done!");
    }
}