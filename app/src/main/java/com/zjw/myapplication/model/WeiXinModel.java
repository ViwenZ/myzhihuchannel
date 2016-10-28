package com.zjw.myapplication.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjw.myapplication.entity.WeiXin;
import com.zjw.myapplication.utils.OnFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/10/21.
 */

public class WeiXinModel {

    public void getWeiXins(int page, final OnFinishListener listener) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/weixin/")
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        WeiXinService weiXinService = retrofit.create(WeiXinService.class);
        Call<String> callStr = weiXinService.getWeiXinStr("7bf9c73f7db932e245ca1f2664745d4a", 10, page);
        callStr.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonString = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String reason = jsonObject.getString("reason");
                    if ("success".equals(reason)) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        if (result != null) {
                            String list = result.getString("list");
                            Gson gson = new Gson();
                            List<WeiXin> weiXins = gson.fromJson(list, new TypeToken<List<WeiXin>>() {
                            }.getType());
                            listener.onSuccess(weiXins);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("zjw", "请求失败，原因是：" + t.getMessage());
            }
        });
        return;
    }
}

interface WeiXinService {
    @GET("query")
    Call<String> getWeiXinStr(@Query("key") String key, @Query("ps") int ps, @Query("pno") int pno);
}


