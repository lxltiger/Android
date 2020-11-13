package com.lxl.essence.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import com.lxl.essence.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
        val title: TextView = findViewById(R.id.title)
        val taps: TextView = findViewById(R.id.taps)
        val spinner: ProgressBar = findViewById(R.id.spinner)
        val titleViewModel = ViewModelProviders.of(this).get(TitleViewModel::class.java)
        rootLayout.setOnClickListener{titleViewModel.onMainClick()}

    }
}


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
        viewModelScope.launch {
            count++
            delay(1000)
            _tapCount.value=("$count tap")
        }

    }

    private fun refreshTitle() {


    }


}


class TitleRepository {

    val title = MutableLiveData<String?>()




}

interface Network{
    @GET("local.json")
    suspend fun getTitle():String

}

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