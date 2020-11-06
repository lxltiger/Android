package com.lxl.essence.coroutines

import okhttp3.Interceptor
import okhttp3.Response


private val FAKE_LIST = listOf("Hello, coroutines!",
        "My favorite feature",
        "Async made easy",
        "Coroutines by example",
        "Check out the Advanced Coroutines codelab next!")

class SkipNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        TODO("Not yet implemented")
    }


    private fun blocking(){
        Thread.sleep(500)
    }

}