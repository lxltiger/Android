package com.lxl.essence.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TitleViewModel : ViewModel() {
    private var count = 0;

    private val _tapCount = MutableLiveData<String>("$count tap")

    val tapCount: LiveData<String>
        get() = _tapCount

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner


    fun onMainClick(){
        refreshTitle();
        updateCount();
    }

    private fun updateCount() {


    }

    private fun refreshTitle() {


    }


}