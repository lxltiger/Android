package com.lxl.essence.paging.lib2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.lxl.essence.paging.Article;
import com.lxl.essence.vo.State;

public class ItemViewModel extends ViewModel {

    LiveData<PagedList<Article>> itemPagedList;
    LiveData<ItemDataSource> dataSourceLiveData;
    private final ItemDataSourceFactory itemDataSourceFactory;

    public ItemViewModel() {
        itemDataSourceFactory = new ItemDataSourceFactory();
        dataSourceLiveData = itemDataSourceFactory.getItemLiveDataSource();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(ItemDataSource.PAGE_SIZE)
                .build();

        itemPagedList = new LivePagedListBuilder<>(itemDataSourceFactory, config).build();

    }

    public void fresh() {
        itemPagedList.getValue().getDataSource().invalidate();
    }

    public void setQuery(String query) {
        itemDataSourceFactory.setQuery(query);
        itemPagedList.getValue().getDataSource().invalidate();
    }

    public boolean isEmpty() {
        if (itemPagedList.getValue() != null) {
            return itemPagedList.getValue().isEmpty();
        }
        return true;
    }
    public LiveData<State> getState() {
        return Transformations.switchMap(dataSourceLiveData, ItemDataSource::getState);
    }


}
