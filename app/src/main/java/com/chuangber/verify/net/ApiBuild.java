package com.chuangber.verify.net;

import com.chuangber.verify.application.ConStant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**retrofit实例
 * Created by jinyh on 2018/4/17.
 */

public class ApiBuild {

    static Retrofit retrofit;
    public static Retrofit getRetrofit(){

        //创建 Retrofit 实例
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.writeTimeout(30 * 1000, TimeUnit.MILLISECONDS);
        client.readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        client.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS);
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(ConStant.URL_SERVER)
                    .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                    .client(client.build()) // 如不添加 默认okHttp
                    .build();
        }

        return retrofit;
    }

    public static ApiService getService(){
        ApiService service = getRetrofit().create(ApiService.class);
        return service;
    }

}
