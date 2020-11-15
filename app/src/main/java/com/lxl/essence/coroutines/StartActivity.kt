package com.lxl.essence.coroutines

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lxl.essence.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
private val TAG = "kotlin-coroutine"

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
        val title: TextView = findViewById(R.id.title)
        val taps: TextView = findViewById(R.id.taps)
        val spinner: ProgressBar = findViewById(R.id.spinner)

        val database = getDatabase(this)
        val repository = TitleRepository(getApiService(), database.titleDao)
        val titleViewModel = ViewModelProviders.of(this,
                TitleViewModel.FACTORY(repository))
                .get(TitleViewModel::class.java)

        rootLayout.setOnClickListener { titleViewModel.onMainClick() }
        titleViewModel.tapCount.observe(this, Observer { text ->
            taps.text = text;
        })

        titleViewModel.spinner.observe(this, Observer { value ->
            value.let {
                spinner.visibility = if (value) View.VISIBLE else View.GONE
            }
        })
        titleViewModel.title.observe(this, Observer {value->
            value?.let {
                title.text = it.title
            }
        })

        titleViewModel.snackBar.observe(this, Observer {value->
            value?.let{
                Snackbar.make(rootLayout,it,Snackbar.LENGTH_SHORT).show()
                titleViewModel.onSnackBarShown()
            }

        })


    }
}

//todo 还不知道啥意思
fun <T : ViewModel, A> singleArgFactory(constructor: (A) -> T):
        (A) -> ViewModelProvider.NewInstanceFactory {
    return { arg: A ->
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <V : ViewModel?> create(modelClass: Class<V>): V {
                return constructor(arg) as V
            }
        }

    }
}

class TitleViewModel(val titleRepository: TitleRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgFactory(::TitleViewModel)
    }

    val title = titleRepository.title

    private var count = 0;

    private val _tapCount = MutableLiveData<String>("$count tap")

    val tapCount: LiveData<String>
        get() = _tapCount

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _snackBar = MutableLiveData<String?>()

    val snackBar: LiveData<String?>
        get() = _snackBar


    fun onMainClick() {
        refreshTitle();
        updateCount();
    }

    private fun updateCount() {
        viewModelScope.launch {
            count++
            delay(1000)
            _tapCount.value = ("$count tap")
        }

    }

    private fun refreshTitle() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                titleRepository.refreshTitle()
            } catch (error: TitleRefreshError) {
                Log.d(TAG, "refreshTitle: "+error.cause?.message)
                _snackBar.value = error.message
            } finally {
                _spinner.value = false
            }
        }

    }

    fun onSnackBarShown() {
        _snackBar.value=null
    }


}


class TitleRepository(val network: MainNetwork, val titleDao: TitleDao) {
    val title: LiveData<Title?> = titleDao.titleLiveData


    suspend fun refreshTitle() {
        try {
            val title = withTimeout(5000) {
                network.getTitle()
            }
            Log.d(TAG, "refreshTitle: " + title)
            titleDao.insertTitle(Title(title))
        } catch (e: Throwable) {
            throw TitleRefreshError("unable to refresh", e)
        }
    }


}

class TitleRefreshError(msg: String, e: Throwable) : Throwable(msg, e)

