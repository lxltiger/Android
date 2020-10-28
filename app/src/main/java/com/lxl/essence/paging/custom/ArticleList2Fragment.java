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


import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.lxl.essence.R;
import com.lxl.essence.base.RecyclerViewNoBugLinearLayoutManager;
import com.lxl.essence.databinding.FragmentArticlePageListBinding;
import com.lxl.essence.paging.Article;
import com.lxl.essence.paging.ArticleCallBack;
import com.lxl.essence.paging.Data;
import com.lxl.essence.vo.Resource;

import java.util.List;

/*
 *Web式的分页方式，没有上拉加载 手动选取页数
 * */
public class ArticleList2Fragment extends Fragment {
    private static final String TAG = "AddressListFragment";
    private FragmentArticlePageListBinding binding;
    private ArticleQueryViewModel viewModel;
    private ArticleAdapter articleAdapter;
    private int currentPage = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleAdapter = new ArticleAdapter(articleCallBack);
//        Activity 级别的ViewModle 即使Fragment实例销毁都能获得同样的Model
        viewModel = ViewModelProviders.of(requireActivity()).get(ArticleQueryViewModel.class);
        //页面跳转后视图会被销毁  所以不在视图生命周期内监听数据变化 防止频繁重复加载
        viewModel.results.observe(requireActivity(), new Observer<Resource<Data>>() {
            @Override
            public void onChanged(Resource<Data> resource) {
                Log.d(TAG, "resource: " + resource);
                binding.refresh.setRefreshing(resource.isLoading());
                if (resource.isSucceed()) {
                    handleResponse(resource.data);
                } else if (resource.isError()) {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    private void dismissKeyboard(IBinder windowToken) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    private void onPageTextClick(View view) {
        if (view instanceof RadioButton) {
            RadioButton button = (RadioButton) view;
            button.setChecked(true);
        }
        String query = binding.input.getText().toString();
        dismissKeyboard(view.getWindowToken());
        Integer pageNo = (Integer) view.getTag();
        if (pageNo != null) {
            currentPage=pageNo;
            viewModel.loadPage(currentPage, query);
        }
    }

    private void handleResponse(Data data) {
        List<Article> list = data.getDatas();
        articleAdapter.replace(list);
        binding.addressList.smoothScrollToPosition(0);
        //在获取第一页的时候动态添加页面
        Log.d(TAG, "handleResponse: "+data.getPageCount());
        if (data.getCurPage() == 1) {
            int count = data.getPageCount() - 2;
            binding.container.removeAllViews();
            for (int i = 0; i < count; i++) {
                RadioButton text = (RadioButton) LayoutInflater.from(requireContext()).inflate(R.layout.item_page_radiobtn, binding.container, false);
                text.setText(String.valueOf(i + 2));
                text.setTag(i + 2);

                binding.container.addView(text);
                text.setOnClickListener(this::onPageTextClick);
            }
            binding.end.setTag(Math.max(data.getPageCount(), 1));
        }
        //没有加载到数据
        if (articleAdapter.getItemCount() == 0) {
            binding.noData.setVisibility(View.VISIBLE);
            binding.noData.setText("没有数据");
        } else {
            binding.noData.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_page_list, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.home.setTag(1);
        binding.end.setTag(1);
        binding.home.setOnClickListener(this::onPageTextClick);
        binding.end.setOnClickListener(this::onPageTextClick);
        binding.search.setOnClickListener((v -> {
            String query = binding.input.getText().toString();
            dismissKeyboard(view.getWindowToken());
            viewModel.loadPage(1, query);

        }));
        binding.addressList.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(requireContext()));
        binding.addressList.setAdapter(articleAdapter);
       /* binding.refresh.setOnRefreshListener(() -> {
            Log.d(TAG, "setOnRefreshListener: ");
            viewModel.loadPage(currentPage, "");
        });*/

        //避免从编辑页面返回引起刷新
        if (1 == currentPage) {
            viewModel.loadPage(1, "");
        }
    }


    private ArticleCallBack articleCallBack = new ArticleCallBack() {
        @Override
        public void onItemClick(Article address) {

        }

        @Override
        public void onEditClick(Article address) {

        }
    };

}
