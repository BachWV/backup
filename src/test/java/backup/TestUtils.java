package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestUtils {

    public static boolean fileEquals(File file1, File file2) throws IOException {
        byte[] bytes1 = Files.readAllBytes(file1.toPath());
        byte[] bytes2 = Files.readAllBytes(file2.toPath());
        if (bytes1.length != bytes2.length) {
            return false;
        }

        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }

        return true;
    }
}
