package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Backuper {

    private final Packer packer = new Packer();

    private final HuffmanCompressor compressor = new HuffmanCompressor();

    private final Encryptor encryptor = new Encryptor();

    /**
     * 将 source 目录中的数据备份到 target 目录下
     *
     * @param source 要备份的目录
     * @param target 存放备份文件的目录
     */
    public void backup(String source, String target, String password) throws Exception {
        // 要求 source 和 target 都是存在的目录
        File sourceDir = new File(source);
        File targetDir = new File(target);

        if (!sourceDir.exists()) {
            throw new IOException("文件 " + source + " 不存在");
        }
        if (!targetDir.exists()) {
            throw new IOException("文件 " + target + " 不存在");
        }
        if (!sourceDir.isDirectory()) {
            throw new IOException("文件 " + source + " 不是目录");
        }

        if (!targetDir.isDirectory()) {
            throw new IOException("文件 " + target + " 不是目录");
        }

        String packFilePath = target + File.separator + sourceDir.getName() + ".pack";
        // sourceDir -> sourceDir.pack
        packer.pack(new File(source), new File(packFilePath));
        String compressFilePath = packFilePath + ".huff";
        // sourceDir.pack -> sourceDir.pack.huff
        compressor.compress(new File(packFilePath), new File(compressFilePath));
        String encryptFilePath = compressFilePath + ".enc";
        // sourceDir.pack.huff -> sourceDir.pack.huff.enc
        encryptor.encrypt(new File(compressFilePath), new File(encryptFilePath), password);
        Files.delete(Paths.get(compressFilePath));
        Files.delete(Paths.get(packFilePath));
    }

    /**
     * 将 source 指定的备份文件恢复到 target 目录下
     *
     * @param source 备份文件路径
     * @param target 存放恢复出的文件的目录
     */
    public void restore(String source, String target, String password) throws Exception {
        if (!source.endsWith(".pack.huff.enc")) {
            throw new Exception("备份文件必须以 .pack.huff.enc 结尾");
        }
        String compressFilePath = source.substring(0, source.length() - 4);
        String packFilePath = compressFilePath.substring(0, compressFilePath.length() - 5);
        // sourceDir.pack.huff.enc -> sourceDir.pack.huff
        encryptor.decrypt(new File(source), new File(compressFilePath), password);
        // sourceDir.pack.huff -> sourceDir.pack
        compressor.decompress(new File(compressFilePath), new File(packFilePath));
        // sourceDir.pack -> sourceDir
        packer.unpack(new File(packFilePath), new File(target));
        Files.delete(Paths.get(compressFilePath));
        Files.delete(Paths.get(packFilePath));
    }

}
