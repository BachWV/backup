package backup.Tools;

//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//

import backup.GUI.PanGUI;
import com.alibaba.fastjson.JSONException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtils {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void main() {
        String url="http://pan.junling.xyz/api/public/path";
        String body="{\n" +
                "    \"path\": \"/aliyun/pantest\"\n" +
                "}";
        RequestBody requestBody= RequestBody.create(body, MediaType.parse("application/json"));
        post(url,requestBody,new OkHttpCallback(){
            @Override
            public String onFinish(String status, String msg) throws JSONException {

                return super.onFinish(status, msg);
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
     * 封装get请求(带拦截器)
     * @param url
     * @param callback
     */
    public static void getWithToken(String url,String token, OkHttpCallback callback){
        callback.url = url;
        Request request = new Request.Builder().url(url).addHeader("token",token).build();

    }

    /**
     * 封装post请求(带拦截器)
     * @param url
     * @param body
     * @param callback
     */
    public static void postWithToken(String url, RequestBody body, String token,OkHttpCallback callback){
        callback.url = url;
//        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).post(body).build();
    }


}

