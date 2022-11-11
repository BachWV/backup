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
    public String url;
    public String result;

    /**
     * 接口调用失败回调此方法
     * @param call
     * @param e
     */
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
    }

    /**
     * 接口调用成功回调此方法
     * @param call
     * @param response  接口返回的数据
     * @throws IOException
     */
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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

