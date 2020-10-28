package com.lxl.essence.paging.lib2;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.lxl.essence.paging.Article;

public class ItemDataSourceFactory extends DataSource.Factory<Integer, Article> {
    private static final String TAG = "ItemDataSourceFactory";
    private MutableLiveData<ItemDataSource> itemLiveDataSource = new MutableLiveData<>();
    private String query = "";

    @NonNull
    @Override
    public DataSource<Integer, Article> create() {
        Log.d(TAG, "create: " + query);
        ItemDataSource dataSource = new ItemDataSource(query);
        itemLiveDataSource.postValue(dataSource);
        return dataSource;
    }

    public LiveData<ItemDataSource> getItemLiveDataSource() {
        return itemLiveDataSource;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
