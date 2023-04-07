package com.nvrskdev.myschool.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.data.Question
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionViewModel(application: Application, private val testPath: String) :
    AndroidViewModel(application) {

    private val _questions: MutableStateFlow<MutableList<Question>> =
        MutableStateFlow(mutableListOf())
    val questions = _questions.asStateFlow()

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)

            _questions.value = withContext(Dispatchers.IO) {
                Gson().fromJson(
                    SchoolApi().get("http://www.devnvrsk.ru/api/Tests/filename?filename=${testPath}")
                        .bodyAsText(), object : TypeToken<List<Question>>() {}.type
                )
            }

            _questions.value.forEach {
                it.answers.shuffle()
            }

            _state.value = true
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            testPath: String
        ): ViewModelProvider.AndroidViewModelFactory {
            return object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuestionViewModel(application, testPath) as T
                }
            }
        }
    }
}