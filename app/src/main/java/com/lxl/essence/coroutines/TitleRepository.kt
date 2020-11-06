package com.lxl.essence.coroutines

import androidx.lifecycle.MutableLiveData

class TitleRepository {

    val title = MutableLiveData<String?>()

    fun refreshTitle(onTitleCallBack: OnTitleCallBack) {

    }
}

interface OnTitleCallBack {
    fun onSucceed()
    fun onFail(msg: String)

}