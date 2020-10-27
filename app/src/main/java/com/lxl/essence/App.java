package com.lxl.essence;

import android.app.Application;
import android.util.Log;


import com.lxl.essence.api.FunAndroidService;
import com.lxl.essence.base.AppExecutors;
import com.lxl.essence.paging.ArticleRepository;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static final String TAG = "App";
    private static App lockerApp;
    private AppExecutors appExecutors;
    private static final String BASE_URL = "http://15.80.185.222:8080/";
    private FunAndroidService funAndroidService;
    private ArticleRepository baseRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        lockerApp = this;
        appExecutors = new AppExecutors();
        funAndroidService = provideFunAndroidService();

        baseRepository = new ArticleRepository(appExecutors, this);
    }

    public FunAndroidService funAndroidService() {
        return funAndroidService;
    }


    public ArticleRepository baseRepository() {
        return baseRepository;
    }

    //演示使用
    private FunAndroidService provideFunAndroidService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.i("FunAndroid", "message " + message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);
        OkHttpClient client = builder.addInterceptor(logging).build();
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();
        return new Retrofit.Builder()
                .baseUrl("https://wanandroid.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FunAndroidService.class);
    }




    public static App get() {
        return lockerApp;
    }

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

}
