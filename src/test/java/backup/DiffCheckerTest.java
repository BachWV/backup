package backup;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiffCheckerTest {

    @Test
    public void diffTest() throws Exception {
        Path source = Paths.get(getClass().getResource("/diff/dir1").toURI());
        Path target = Paths.get(getClass().getResource("/diff/dir2").toURI());
        DiffChecker diffChecker = new DiffChecker();
        String diff = diffChecker.dirDiff(source.toString(), target.toString());

        String expected = "file added: a.txt\n" +
                "file deleted: d.txt\n" +
                "file diff: c.txt\n" +
                "dir added: subdir1" + File.separator + "subdir11\n" +
                "dir deleted: subdir1" + File.separator + "subdir12\n" +
                "file diff: subdir1" + File.separator + "a1.txt\n";
        System.out.println(diff);
        Assert.assertEquals(expected, diff);
    }
}
