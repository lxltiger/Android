package com.lxl.essence.base;

import android.view.View;

import androidx.databinding.BindingAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BaseBindingAdapters {
    private static final String TAG = "BindingAdapters";


    @BindingAdapter("visibleGone")
    public static void setVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("invisible")
    public static void setInVisible(View view, boolean invisible) {
        view.setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
    }


}
