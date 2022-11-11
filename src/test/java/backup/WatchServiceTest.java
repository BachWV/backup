package backup;

import backup.Tools.SavedFile;
import backup.Tools.SystemDiff;
import backup.Tools.WatchService;
import org.junit.Test;

import java.io.IOException;

public class WatchServiceTest {
    @Test
    void createTest() {
        if (SystemDiff.isWindows()) {
            SavedFile sf = new SavedFile("D://test", "~/Desktop/test");
            try {
                WatchService.create(sf);
            } catch (IOException e) {
            }

        }else if(SystemDiff.isLinux()){
            SavedFile sf = new SavedFile("/home/ubuntu/Desktop", "~/Download");
            try {
                WatchService.create(sf);
            } catch (IOException e) {
            }

        }
    }
}
