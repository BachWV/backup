package backup;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static backup.Tools.FileHelper.read;
import static backup.Tools.FileHelper.saved;
import static backup.Tools.OkHttpUtils.getCloudFileList;

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
    @Test
    public static void main(String[] args) {
        getCloudFileList();

    }

    @Test
    public void httpget(){

        getCloudFileList();
    }
    @Test
    public void fileHelp() throws FileNotFoundException {
        saved();
        read();
    }

}
