package backup.Tools;

import com.alibaba.fastjson.JSONException;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DownloadFile {
    public static void main(String url,String tagretDir,String filename) {
       OkHttpUtils.get(url,new OkHttpCallback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                byte []bytes=response.body().bytes();
                Path filePath = Paths.get(tagretDir+"\\" +filename);

                    //不存在文件 => 创建
                    Files.write(filePath, bytes, StandardOpenOption.CREATE);

            }
        });

    }
}
