/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxl.essence.paging.custom;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.lxl.essence.App;
import com.lxl.essence.base.HandyLiveData;
import com.lxl.essence.paging.ArticleRepository;
import com.lxl.essence.paging.Data;
import com.lxl.essence.vo.Resource;

import java.util.List;


public class ArticleQueryViewModel extends ViewModel {
    private static final String TAG = "UnrelatedViewModel";
    private ArticleRepository repository;
    private final MutableLiveData<PageQueryRequest> pageQuery = new MutableLiveData<>();
    public final LiveData<Resource<Data>> results;

    //记录当前加载页 防止重复加载
    private int currentPageNo;
    //每页数量不大 最后一屏显示 因为是手动选页
    private int pageSize = 8;

    private final PageQueryRequest request;

    public ArticleQueryViewModel() {
        request = new PageQueryRequest(1, pageSize, "");
        repository = App.get().baseRepository();
        results = Transformations.switchMap(pageQuery, pageQuery -> {
            if (pageQuery != null) {
                return repository.retrieveData(pageQuery);
            } else {
                return HandyLiveData.create(Resource.error("参数为空", null));
            }
        });
    }

    public void loadPage(int pageNo, String query) {
        request.setPageNo(pageNo);
        request.setQuery(query);
        this.pageQuery.setValue(request);
    }

}
