package com.nvrskdev.myschool.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.data.Test
import com.nvrskdev.myschool.datastore.AppDataStore
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestViewModel(application: Application, private val dataStore: AppDataStore) :
    AndroidViewModel(application) {

    private val _tests: MutableStateFlow<MutableList<Test>> = MutableStateFlow(mutableListOf())
    val tests = _tests.asStateFlow()

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state = _state.asStateFlow()

    init {
        fetchTests()
    }

    fun fetchTests() {
        viewModelScope.launch {
            delay(1000)

            dataStore.userId.collect { id ->
                dataStore.userRole.collect { role ->

                    _tests.value = withContext(Dispatchers.IO) {
                        Gson().fromJson(
                            SchoolApi().get("http://www.devnvrsk.ru/api/Tests/${id}/${role}")
                                .bodyAsText(), object : TypeToken<List<Test>>() {}.type
                        )
                    }

                    println(Gson().toJson(_tests.value))

                    _state.value = true
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            dataStore: AppDataStore
        ): ViewModelProvider.AndroidViewModelFactory {
            return object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TestViewModel(application, dataStore) as T
                }
            }
        }
    }
}