package com.lxl.essence.paging.custom;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lxl.essence.R;
import com.lxl.essence.base.DataBoundViewHolder;
import com.lxl.essence.databinding.ItemArticleBinding;
import com.lxl.essence.paging.Article;
import com.lxl.essence.paging.ArticleCallBack;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<DataBoundViewHolder<ItemArticleBinding>> {

    private ArticleCallBack callBack;
    private List<Article> list;

    public ArticleAdapter(ArticleCallBack callBack) {
        this.callBack = callBack;
        list = new ArrayList<>();
    }


    public void addAll(List<Article> datas) {
        if (datas != null&&!datas.isEmpty()) {
            this.list.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void modify(Article metaDz) {
        if (metaDz != null) {
//            this.list.set(metaDz.getPos(), metaDz);
            notifyItemChanged(metaDz.getPos());
        }
    }

    public void remove(int pos) {
        this.list.remove(pos);
        //重新设置索引
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DataBoundViewHolder<ItemArticleBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticleBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_article, parent, false);
        binding.setCallback(callBack);
        return new DataBoundViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<ItemArticleBinding> holder, int position) {
        Article metaDz = list.get(position);
        metaDz.setPos(position);
        holder.binding.setArticle(metaDz);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }
}
