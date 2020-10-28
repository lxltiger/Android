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


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.lxl.essence.R;
import com.lxl.essence.base.RecyclerViewNoBugLinearLayoutManager;
import com.lxl.essence.databinding.FragmentArticleListBinding;
import com.lxl.essence.paging.Article;
import com.lxl.essence.paging.ArticleCallBack;
import com.lxl.essence.vo.Resource;

import java.util.List;

/*
 *简单的分页列表，数据来源自网络 缓存在内存 没有使用数据库
 *支持对列表的修改
 * */
public class ArticleListFragment extends Fragment {
    private static final String TAG = "AddressListFragment";
    private FragmentArticleListBinding binding;
    private ArticleViewModel viewModel;
    private ArticleAdapter articleAdapter;
    private long startTime = 0;
    private int currentPage = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleAdapter = new ArticleAdapter(articleCallBack);
//        Activity 级别的ViewModle 即使Fragment实例销毁都能获得同样的Model
        viewModel = ViewModelProviders.of(requireActivity()).get(ArticleViewModel.class);
        //页面跳转后视图会被销毁  所以不在视图生命周期内监听数据变化 防止频繁重复加载
        viewModel.articles.observe(requireActivity(), new Observer<Resource<List<Article>>>() {
            @Override
            public void onChanged(Resource<List<Article>> resource) {
                Log.d(TAG, "resource: " + resource);
                if (resource.isLoading()) {
                    resetLoadStatus(true);
                } else if (resource.isSucceed()) {
                    resetLoadStatus(false);
                    List<Article> data = resource.data;
                    articleAdapter.addAll(data);
                    //没有加载到数据
                    if (articleAdapter.getItemCount() == 0) {
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.noData.setText("没有数据");
                    }
                    if (data.size() > 0) {
                        currentPage++;
                    } else {
                        //使用currentPage标记数据的结束
                        currentPage = -1;
                        Toast.makeText(requireContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //error
                    if (articleAdapter.getItemCount() == 0) {
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.noData.setText(resource.message);
                        binding.refresh.setRefreshing(false);
                    } else {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                        binding.setLoadingMore(false);
                    }
                }
            }

        });

//        和修改页面公用数据
        viewModel.articleMutableLiveData.observe(requireActivity(), new Observer<Article>() {
            @Override
            public void onChanged(Article articlePair) {
                Log.d(TAG, "onChanged: article" + articlePair);
                if (articlePair != null && articlePair.isModified()) {
                    articleAdapter.modify(articlePair);
                    //防止下次被错误观察到
                    viewModel.setArticle(null);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, container, false);
        return binding.getRoot();
    }


    public void onTitleDoubleClick(View view) {
        if (System.currentTimeMillis() - startTime < 350) {
            binding.addressList.scrollToPosition(0);
        } else {
            startTime = System.currentTimeMillis();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addressList.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(requireContext()));
        binding.addressList.setAdapter(articleAdapter);
        binding.addressList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (lastPosition == articleAdapter.getItemCount() - 1) {
                    //滑到底部 还有数据  此回调会多次执行
                    Log.d(TAG, "onScrolled: to end" + currentPage);
                    if (currentPage > 0) {
                        viewModel.loadingMore(currentPage);
                    }
                }
            }
        });

        binding.refresh.setOnRefreshListener(() -> {
            Log.d(TAG, "setOnRefreshListener: ");
            articleAdapter.clear();
            currentPage = 1;
            viewModel.loadFirst();
        });

        binding.title.setOnClickListener(this::onTitleDoubleClick);

        Log.d(TAG, "currentPage: " + currentPage);
        //避免从编辑页面返回引起刷新
        if (1 == currentPage) {
            viewModel.loadFirst();
        }

    }


    private void resetLoadStatus(boolean loading) {
        if (articleAdapter.getItemCount() == 0) {
            binding.refresh.setRefreshing(loading);
        } else {
            binding.setLoadingMore(loading);
        }
    }

    private ArticleCallBack articleCallBack = new ArticleCallBack() {
        @Override
        public void onItemClick(Article address) {
            address.setModified(false);
            viewModel.setArticle(address);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_to_modifyFragment);
        }

        @Override
        public void onEditClick(Article address) {
            articleAdapter.remove(address.getPos());
        }
    };

}
