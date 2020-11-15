package com.lxl.essence.coroutines

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
private val TAG = "Network"

private val service: MainNetwork by lazy {
    val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(SkipNetworkInterceptor())
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    retrofit.create(MainNetwork::class.java)
}

fun getApiService() = service
//注意retrofit的版本，如不不支持协程，会抛出错误：Unable to create call adapter
interface MainNetwork {
    @GET("local.json")
    suspend fun getTitle(): String

}

private val FAKE_LIST = listOf("Hello, coroutines!",
        "My favorite feature",
        "Async made easy",
        "Coroutines by example",
        "Check out the Advanced Coroutines codelab next!")

class SkipNetworkInterceptor : Interceptor {

    private val gson = Gson()
    private var count = 0
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(TAG, "intercept: ")
        blocking()
        count++
        return if ((count % 5) == 0) {
            makeFailResult(chain.request())
        } else {
            makeOkResult(chain.request())
        }
    }


    private fun blocking() {
        Thread.sleep(500)
    }

    private fun makeOkResult(request: Request): Response {
        val result = FAKE_LIST.random()
        return Response.Builder()
                .code(200)
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message("ok")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"),
                        gson.toJson(result))).build()
    }

    private fun makeFailResult(request: Request): Response {
        return Response.Builder()
                .code(500)
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message("bad gateway")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"),
                        gson.toJson(mapOf("case" to "not sure")))).build()
    }

}