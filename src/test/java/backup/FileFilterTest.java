package backup;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileFilterTest {

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
}
