package backup.Tools;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;


public class OkHttpUtils {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void getCloudFileList() {
        String url="http://pan.junling.xyz/api/public/path";
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


}

