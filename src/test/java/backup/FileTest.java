package backup;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static backup.Tools.FileHelper.read;
import static backup.Tools.FileHelper.saved;

public class FileTest {

    @Test
    public void fileFileTest() throws URISyntaxException {
        Path root = Paths.get(getClass().getResource("/filter").toURI());
        FileFilter filter = new FileFilter(root);
        Assert.assertTrue(filter.filter(root.resolve("foo.jpg")));
        Assert.assertFalse(filter.filter(root.resolve("foo.png")));
        Assert.assertTrue(filter.filter(root.resolve("foo.mp3")));
        Assert.assertTrue(filter.filter(root.resolve("a")));
        Assert.assertTrue(filter.filter(root.resolve("a/b")));
        Assert.assertFalse(filter.filter(root.resolve("a/c")));
    }

    @Test
    public void fileHelp() throws FileNotFoundException {
        saved();
        read();
    }
}
