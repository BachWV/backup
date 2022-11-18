package backup;

import org.junit.Test;

import static backup.Tools.OkHttpUtils.getCloudFileList;

public class HttpTest {
    @Test
    public void httpget(){

        getCloudFileList();
    }

    @Test
    public void getFileTest() {
        getCloudFileList();

    }
}
