package com.nvrskdev.myschool.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.data.Teacher
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeacherViewModel : ViewModel() {
    private val _teachers: MutableStateFlow<MutableList<Teacher>> =
        MutableStateFlow(mutableListOf())
    val teachers = _teachers.asStateFlow()

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)

            _teachers.value = withContext(Dispatchers.IO) {
                Gson().fromJson(
                    SchoolApi().get("http://www.devnvrsk.ru/api/Teachers").bodyAsText(),
                    object : TypeToken<List<Teacher>>() {}.type
                )
            }

            _state.value = true
        }
    }
}