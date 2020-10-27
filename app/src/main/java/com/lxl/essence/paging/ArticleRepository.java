package com.lxl.essence.paging;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.lxl.essence.App;
import com.lxl.essence.api.FunAndroidService;
import com.lxl.essence.base.AppExecutors;
import com.lxl.essence.vo.Resource;

import java.util.List;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleRepository {
    private static final String TAG = "BaseRepository";
    public static final MediaType MEDIATYPE = MediaType.parse("application/json; charset=utf-8");
    private AppExecutors appExecutors;
    private FunAndroidService baseService;

    public ArticleRepository(AppExecutors appExecutors, App app) {
        this.appExecutors = appExecutors;
        this.baseService = app.funAndroidService();
    }


    public LiveData<Resource<List<Article>>> retrieveArticle(int page) {
        Log.d(TAG, "test: ");
        MutableLiveData<Resource<List<Article>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        App.get().funAndroidService().getArticle(page,"Java").enqueue(new Callback<FunAndroidResponse>() {
            @Override
            public void onResponse(Call<FunAndroidResponse> call, Response<FunAndroidResponse> response) {
                FunAndroidResponse body = response.body();
                if (body != null&&body.getData()!=null) {
                    result.setValue(Resource.success(body.getData().getDatas()));
                }else {
                    result.setValue(Resource.error("返回数据异常",null));
                }
            }

            @Override
            public void onFailure(Call<FunAndroidResponse> call, Throwable t) {
                result.setValue(Resource.error("网络请求失败", null));
            }
        });
        return result;
    }
}
