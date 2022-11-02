package backup;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class HuffmanCompressorTest {

    @Test
    public void compressTest() throws URISyntaxException, IOException {
        File original = new File(getClass().getResource("/huffman/linux_makefile.txt").toURI());
        File compressed = File.createTempFile("compressed-", ".huff");
        File decompressed = File.createTempFile("decompressed-", ".txt");
        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(original, compressed);
        compressor.decompress(compressed, decompressed);
        System.out.printf("original size: %dB, compressed size: %dB, rate: %.2f%%\n",
                original.length(), compressed.length(), 100.0 * compressed.length() / original.length());


        // 确定解压还原后的文件内容与原来的文件相同
        Assert.assertTrue(TestUtils.fileEquals(original, decompressed));

        compressed.deleteOnExit();
        decompressed.deleteOnExit();
    }
}
