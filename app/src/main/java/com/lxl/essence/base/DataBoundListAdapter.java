package com.lxl.essence.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public abstract class DataBoundListAdapter<T, V extends ViewDataBinding> extends ListAdapter<T, DataBoundViewHolder<V>> {
    private AppExecutors appExecutors;
    protected DataBoundListAdapter(AppExecutors appExecutors, @NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(new AsyncDifferConfig.Builder<>(diffCallback).setBackgroundThreadExecutor(appExecutors.diskIO()).build());
        this.appExecutors=appExecutors;
    }


    protected void withDataBinding(V dataBinding) {
    }

    protected abstract void bindData(V binding, T item, int pos);


    protected abstract @LayoutRes
    int getLayout();

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V v = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), getLayout(), parent, false);
        withDataBinding(v);
        return new DataBoundViewHolder<>(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        bindData(holder.binding, getItem(position), position);
        holder.binding.executePendingBindings();
    }


}
