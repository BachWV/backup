package backup;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 简单加密 / 解密
 */
public class Encryptor {

    public void encrypt(File source, File target, String password) throws Exception {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(target))) {
            // 写入密码的 hashcode，能快速判断密码错误
            out.writeInt(password.hashCode());
        }
        byte[] bytes = Files.readAllBytes(source.toPath());
        byte[] encrypted = encrypt(bytes, password);
        Files.write(target.toPath(), encrypted, StandardOpenOption.APPEND);
    }


    public void decrypt(File source, File target, String password) throws Exception {
        try (DataInputStream in = new DataInputStream((new FileInputStream(source)))) {
            int hash = in.readInt();
            if (password.hashCode() != hash) {
                throw new Exception("密码错误");
            }
        }
        byte[] bytes = Files.readAllBytes(source.toPath());
        // 前 4 个字节是 hashcode，需要跳过
        byte[] decrypted = decrypt(bytes, 4, bytes.length - 4, password);
        Files.write(target.toPath(), decrypted);
    }

    /**
     * 加密思路：将 byte 变为 f(byte, password)
     * 同时对于每个位置 i，如果满足 g(i, password) = 0，则在 i 处写入随机 byte 进行混淆
     */
    private byte[] encrypt(byte[] data, String password) {
        Random random = new Random(System.currentTimeMillis());
        List<Byte> list = new ArrayList<>();
        long hash = password.hashCode();
        int mod = (int) (hash % 5) + 5;
        int mod1 = (int) (hash % 66);
        for (int i = 0, j = 0; i < data.length; j++) {
            if (j % mod == 0) {
                list.add((byte) (random.nextInt() % 256 - 128));
            } else {
                list.add((byte) ((data[i] + 128 + mod1) % 256 - 128));
                i++;
            }
        }

        byte[] out = new byte[list.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = list.get(i);
        }

        return out;
    }

    /**
     * 解密思路：根据 password 找到混淆的位置（计算满足 g(i, password) = 0 的位置），跳过这些位置
     * 然后通过 f(byte, password) 的反函数还原数据
     */
    private byte[] decrypt(byte[] data, int start, int length, String password) {
        List<Byte> list = new ArrayList<>();
        long hash = password.hashCode();
        int mod = (int) (hash % 5) + 5;
        int mod1 = (int) (hash % 66);
        for (int i = 0; i < length; i++) {
            if (i % mod == 0) continue;
            list.add((byte) ((data[i + start] + 128 + 256 - mod1) % 256 - 128));
        }

        byte[] out = new byte[list.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = list.get(i);
        }
        return out;
    }

}
