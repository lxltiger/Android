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


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.lxl.essence.App;
import com.lxl.essence.base.HandyLiveData;
import com.lxl.essence.paging.Article;
import com.lxl.essence.paging.ArticleRepository;
import com.lxl.essence.vo.Resource;

import java.util.List;


public class ArticleViewModel extends ViewModel {
    private static final String TAG = "UnrelatedViewModel";
    private final MutableLiveData<Integer> pageNo = new MutableLiveData<>();

    public final LiveData<Resource<List<Article>>> articles;
    private ArticleRepository repository;
    public final MutableLiveData<Article> articleMutableLiveData = new MutableLiveData<>();
    private int currentPageNo;

    public ArticleViewModel() {
        repository = App.get().baseRepository();


        articles = Transformations.switchMap(pageNo, pageNo -> {
            if (pageNo != null && pageNo >= 0) {
                return repository.retrieveArticle(pageNo);
            } else {
                return HandyLiveData.create(Resource.error("页号异常", null));
            }
        });


    }


    public void loadFirst() {
        currentPageNo = 1;
        this.pageNo.setValue(1);
    }

    //
    public void loadingMore(int pageNo) {
        //为了避免重复执行 拒绝相同页的请求
        if (currentPageNo == pageNo) {
            return;
        }
        currentPageNo = pageNo;
        Log.d(TAG, "request: ");
        this.pageNo.setValue(pageNo);
    }

    public void setArticle(Article article) {
        articleMutableLiveData.setValue(article);
    }

}
