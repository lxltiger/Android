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

package com.lxl.essence.paging.lib2;


import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lxl.essence.R;
import com.lxl.essence.base.RecyclerViewNoBugLinearLayoutManager;
import com.lxl.essence.databinding.FragmentArticleQueryListBinding;
import com.lxl.essence.paging.Article;
import com.lxl.essence.vo.State;

public class ArticleListFragment extends Fragment {
    private static final String TAG = "AddressListFragment";
    FragmentArticleQueryListBinding binding;
    private ItemViewModel viewModel;
    private ArticleAdapter articleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_query_list, container, false);
        return binding.getRoot();
    }

    private long startTime = 0;


    public void onTitleDoubleClick(View view) {
        if (System.currentTimeMillis() - startTime < 350) {
            binding.addressList.scrollToPosition(0);
        }else{
            startTime= System.currentTimeMillis();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addressList.setLayoutManager(new LinearLayoutManager(requireContext()));

        articleAdapter = new ArticleAdapter(requireActivity());

        viewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        viewModel.itemPagedList.observe(getViewLifecycleOwner(), new Observer<PagedList<Article>>() {
            @Override
            public void onChanged(PagedList<Article> items) {
                articleAdapter.submitList(items);
            }
        });

        binding.addressList.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(requireContext()));
        binding.addressList.setAdapter(articleAdapter);
        initSearchInputListener();
        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refresh.setRefreshing(true);
                viewModel.fresh();
            }
        });

        binding.search.setOnClickListener((this::doSearch));

        binding.title.setOnClickListener(this::onTitleDoubleClick);

        setState();

    }

    private void setState() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<State>() {
            @Override
            public void onChanged(State state) {
                if (viewModel.isEmpty()) {
                    if (State.LOADING == state) {
                        binding.noData.setVisibility(View.GONE);
                        binding.refresh.setRefreshing(true);
                    } else if (State.DONE == state) {
                        binding.refresh.setRefreshing(false);
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.noData.setText("没有数据");
                    } else if (State.ERROR == state) {
                        binding.refresh.setRefreshing(false);
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.noData.setText("网络请求失败");
                    }
                }else{
                    binding.refresh.setRefreshing(false);
                    binding.noData.setVisibility(View.GONE);
                    articleAdapter.setState(state);
                }
            }
        });

    }
    private void initSearchInputListener() {
        binding.input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(v);
                return true;
            }
            return false;
        });
        binding.input.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                doSearch(v);
                return true;
            }
            return false;
        });
    }

    private void doSearch(View v) {
        String query = binding.input.getText().toString();
        dismissKeyboard(v.getWindowToken());
        viewModel.setQuery(query);
    }


    private void dismissKeyboard(IBinder windowToken) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }
}
