package com.lxl.essence.paging.lib2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lxl.essence.R;
import com.lxl.essence.base.Common;
import com.lxl.essence.paging.Article;
import com.lxl.essence.vo.State;

public class ArticleAdapter extends PagedListAdapter<Article, RecyclerView.ViewHolder> {

    private Context mCtx;
    private int DATA_VIEW_TYPE = 1;
    private int FOOTER_VIEW_TYPE = 2;

    private State state = State.LOADING;

    ArticleAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_VIEW_TYPE) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_article, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_list_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < super.getItemCount() ? DATA_VIEW_TYPE : FOOTER_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DATA_VIEW_TYPE) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.bind(getItem(position));
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bind(state);
        }
    }

    public int getItemCount() {
        return super.getItemCount() + (hasFooter() ? 1 : 0);
    }

    private boolean hasFooter() {
        return super.getItemCount() != 0 && (state == State.LOADING || state == State.ERROR);
    }

    public void  setState(State state) {
        this.state = state;
        notifyItemChanged(super.getItemCount());
//        notifyDataSetChanged();
    }

    private static DiffUtil.ItemCallback<Article> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Article>() {
                @Override
                public boolean areItemsTheSame(Article oldItem, Article newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(Article oldItem, Article newItem) {
                    return Common.equals(oldItem, newItem);
                }
            };

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView author;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.full_info);
            author = itemView.findViewById(R.id.author);
        }

        public void bind(Article article) {
            if (article != null) {
                textView.setText(article.getTitle());
                author.setText(article.getAuthor());
            }
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        Button button;
        ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.txt_error);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }

        void bind(State state) {
            progressBar.setVisibility(state == State.LOADING ? View.VISIBLE : View.INVISIBLE);
            button.setVisibility(state == State.ERROR ? View.VISIBLE : View.INVISIBLE);
//            progress_bar.visibility = if (status == LOADING) VISIBLE else INVISIBLE
//            itemView.txt_error.visibility = if (status == ERROR) VISIBLE else INVISIBLE
        }
    }
}