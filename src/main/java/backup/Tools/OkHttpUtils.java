package backup.Tools;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;


public class OkHttpUtils {
    private static final String domain = new String("120.24.176.162"); 
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void getCloudFileList() {
        String url="http://"+domain+"/api/public/path";
        String body="{\n" +
                "    \"path\": \"/aliyun/pantest\"\n" +
                "}";
        RequestBody requestBody= RequestBody.create(body, MediaType.parse("application/json"));
        post(url,requestBody,new OkHttpCallback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                result = Objects.requireNonNull(response.body()).string();
                System.out.println(result);
                JSONObject object= com.alibaba.fastjson.JSON.parseObject(result);
                JSONObject data= object.getJSONObject("data");
                System.out.println(data);

                JSONArray files= (JSONArray) data.get("files");
                FileHelper.panList.clear();
                for(int i=0;i<files.size();i++){
                    JSONObject ooo=files.getJSONObject(i);
                    System.out.println(ooo);
                    String filename=ooo.getString("name");

                    String trueUrl=ooo.getString("url");
                    System.out.println(trueUrl);
                    FileHelper.panList.add(new CloudFile(filename,trueUrl));

                }
            }

        });
    }

    /**
     * 封装get请求(不带拦截器)
     * @param url
     * @param callback
     */
    public static void get(String url, OkHttpCallback callback){

        callback.url = url;
        Request request = new Request.Builder().addHeader("Connection","close").url(url).build();
        CLIENT.newCall(request).enqueue(callback);

    }


    /**
     * 封装post请求(不带拦截器)
     * @param url
     * @param body
     * @param callback
     */
    public static void post(String url, RequestBody body, OkHttpCallback callback){
        callback.url = url;
//        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).post(body).build();
        CLIENT.newCall(request).enqueue(callback);
    }
    /**
     * multipart/form-data表单多文件+多参数
     * @author ljl
     */
    public static void MultipartFileUploadPost(String filepath){
        String url = "http://"+domain+"/api/public/upload";

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

    public static void downloadFile(String url, String tagretDir, String filename) {
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

