package com.lxl.essence.base;

import androidx.lifecycle.LiveData;

public class HandyLiveData<T> extends LiveData<T> {


    public static  <T> HandyLiveData<T> create(T t) {
        HandyLiveData<T> liveData=new HandyLiveData<>();
        liveData.postValue(t);
        return liveData;
    }




}
