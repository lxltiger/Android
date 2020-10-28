package com.lxl.essence.paging.custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.lxl.essence.R;
import com.lxl.essence.databinding.FragmentModifyBinding;
import com.lxl.essence.paging.Article;

/*
 *
 * */
public class ModifyFragment extends Fragment {
    private static final String TAG = "FullInformationFragment";
    private FragmentModifyBinding binding;
    private ArticleViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_modify, container, false);
        binding.title.setOnClickListener((v) -> {
            Article article = binding.getArticle();
            article.setTitle("test");
            article.setModified(true);
            viewModel.setArticle( article);
            Navigation.findNavController(binding.getRoot()).navigateUp();
        });

        return binding.getRoot();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(ArticleViewModel.class);
        viewModel.articleMutableLiveData.observe(getViewLifecycleOwner(), new Observer<Article>() {
            @Override
            public void onChanged( Article article) {
                if (article != null) {
                    binding.setArticle(article);
                }
            }
        });
    }

}