package backup.Tools;


        import java.io.File;
        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.concurrent.TimeUnit;

        import okhttp3.MediaType;
        import okhttp3.MultipartBody;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

/**
 * multipart/form-data表单多文件+多参数
 * @author ouyangjun
 */
public class MultipartFileUploadTest {


    public static void send(String filepath){
        String url = "http://pan.junling.xyz/api/public/upload";

        // 请求参数
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("files", new File(filepath));
        paramsMap.put("path", "/aliyun/pantest");
        httpMethod(url, paramsMap);

    }

    public static void httpMethod(String url, Map<String, Object> paramsMap) {
        // 创建client对象 创建调用的工厂类 具备了访问http的能力
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS) // 设置超时时间
                .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(60, TimeUnit.SECONDS) // 设置写入超时时间
                .build();

        // 添加请求类型
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MediaType.parse("multipart/form-data"));

        //  创建请求的请求体
        for (String key : paramsMap.keySet()) {
            // 追加表单信息
            Object object = paramsMap.get(key);
            if (object instanceof File) {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(file, null));
            } else {
                builder.addFormDataPart(key, object.toString());
            }
        }
        RequestBody body = builder.build();

        // 创建request, 表单提交
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 创建一个通信请求
        try (Response response = client.newCall(request).execute()) {
            // 尝试将返回值转换成字符串并返回
            System.out.println("==>返回结果: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}