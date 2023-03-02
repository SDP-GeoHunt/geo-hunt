package com.github.geohunt.app.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.github.geohunt.app.model.BoredRepository
import com.github.geohunt.app.model.api.BoredActivity
import com.github.geohunt.app.model.api.BoredApi
import com.github.geohunt.app.model.persistence.BoredDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The {@link BoredViewModel} is responsible for connecting the UI with the underlying model.
 *
 * @param appContext The application context used to start the database.
 */
class BoredViewModel(appContext: Context) : ViewModel() {
    private val database: BoredDatabase by lazy {
        Room.databaseBuilder(appContext, BoredDatabase::class.java, "bored-activities").build()
    }

    private val repository: BoredRepository by lazy {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: BoredApi = retrofit.create(BoredApi::class.java)

        BoredRepository(api, database.activityDao())
    }

    private val _currentActivity = MutableLiveData<BoredActivity>()

    /**
     * Returns the current activity as {@link LiveData} observable by the UI.
     */
    val currentActivity: LiveData<BoredActivity>
        get() = _currentActivity

    /**
     * Fetches a new random activity from the network, or from the cache if the network call fails.
     *
     * @see BoredRepository.getActivity
     */
    fun getActivity() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentActivity.postValue(repository.getActivity())
        }
    }

    /**
     * Returns true if the last fetch was cached.
     */
    fun isCached(): Boolean = repository.isCached()
}