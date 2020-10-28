package com.lxl.essence.paging.lib2;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.lxl.essence.App;
import com.lxl.essence.paging.Article;
import com.lxl.essence.paging.Data;
import com.lxl.essence.paging.FunAndroidResponse;
import com.lxl.essence.vo.State;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDataSource extends PageKeyedDataSource<Integer, Article> {
    private static final String TAG = "ItemDataSource";
    public static final int PAGE_SIZE = 20;
    private static final int FIRST_PAGE = 0;
    private String query ="";

    public ItemDataSource(String key) {
        this.query = key;
    }

    private MutableLiveData<State> state = new MutableLiveData<>();

    private void updateState(State state) {
        this.state.postValue(state);
    }

    public LiveData<State> getState() {
        return state;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Article> callback) {
        Log.d(TAG, "loadInitial: ");
        updateState(State.LOADING);
        App.get().funAndroidService().getArticle(FIRST_PAGE, query).enqueue(new Callback<FunAndroidResponse>() {
            @Override
            public void onResponse(Call<FunAndroidResponse> call, Response<FunAndroidResponse> response) {
                FunAndroidResponse body = response.body();
                updateState(State.DONE);
                if (body != null&&body.getData()!=null) {
                   callback.onResult(body.getData().getDatas(),null,FIRST_PAGE+1);
                }
            }

            @Override
            public void onFailure(Call<FunAndroidResponse> call, Throwable t) {
                updateState(State.ERROR);
            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Article> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Article> callback) {
        Log.d(TAG, "loadAfter: "+params.key);
        updateState(State.LOADING);
        App.get().funAndroidService().getArticle(params.key, query).enqueue(new Callback<FunAndroidResponse>() {
            @Override
            public void onResponse(Call<FunAndroidResponse> call, Response<FunAndroidResponse> response) {
                FunAndroidResponse body = response.body();
                updateState(State.DONE);

                if (body != null&&body.getData()!=null) {
                    Data data = body.getData();
                    Integer key =  data.getCurPage()<data.getPageCount()? params.key + 1 : null;
                    callback.onResult(body.getData().getDatas(),key);
                }else{
                    Log.d(TAG, "onResponse: is null");
                }
            }

            @Override
            public void onFailure(Call<FunAndroidResponse> call, Throwable t) {
                updateState(State.ERROR);
            }
        });
    }
}
