package backup;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class EncryptorTest {

    @Test
    public void testEncrypt() throws Exception {
       // File original = new File(getClass().getResource("/encrypt/linux_makefile.txt").toURI());
        File original = new File(getClass().getResource("/encrypt/abc.txt").toURI());
        System.out.println(original);
        File encrypted = File.createTempFile("encrypted-", ".enc");
        File decrypted = File.createTempFile("decrypted-", ".txt");
        Encryptor encryptor = new Encryptor();
        encryptor.encrypt(original, encrypted, "123");

        // 使用正确的密码解密，得到的文件内容应与原来一致
        encryptor.decrypt(encrypted, decrypted, "123");
        Assert.assertTrue(TestUtils.fileEquals(original, decrypted));

        // 使用错误的密码解密，应该抛出异常
        try {
            encryptor.decrypt(encrypted, decrypted, "456");
        } catch (Exception e) {
            Assert.assertEquals("密码错误", e.getMessage());
        }
    }
}
