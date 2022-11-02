package backup.Tools;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class OkHttpCallback implements Callback {
    private final String TAG = OkHttpCallback.class.getSimpleName();

    public String url;
    public String result;

    /**
     * 接口调用失败回调此方法
     * @param call
     * @param e
     */
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//       Log.d(TAG,"url:" + url);
//       Log.d(TAG,"请求失败" + e.toString());
//       onFinish("failure",result);
//
//       if (e instanceof SocketTimeoutException) {
//           //判断超时异常
//
//
//       }
//       if (e instanceof ConnectException) {
//           //连接异常
//
//       }


    }

    /**
     * 接口调用成功回调此方法
     * @param call
     * @param response  接口返回的数据
     * @throws IOException
     */
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

        result = Objects.requireNonNull(response.body()).string();
        System.out.println(result);
        JSONObject object= JSON.parseObject(result);
        JSONObject data= object.getJSONObject("data");
        System.out.println(data);

       JSONArray files= (JSONArray) data.get("files");
       for(int i=0;i<files.size();i++){
           JSONObject ooo=files.getJSONObject(i);
           System.out.println(ooo);
          String filename=ooo.getString("name");

          String trueUrl=ooo.getString("url");
           System.out.println(trueUrl);
           FileHelper.panList.add(new CloudFile(filename,trueUrl));

       }



        try {
            onFinish("success",result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String onFinish(String status, String msg) throws JSONException {

        return status;
    }


}

