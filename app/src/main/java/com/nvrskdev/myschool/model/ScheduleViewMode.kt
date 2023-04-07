package com.nvrskdev.myschool.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.data.Lesson
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleViewModel : ViewModel() {
    private val _lessons: MutableStateFlow<MutableList<Lesson>> = MutableStateFlow(mutableListOf())
    val lessons = _lessons.asStateFlow()

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)

            val day = LocalDate.now().dayOfWeek.value

            _lessons.value = withContext(Dispatchers.IO) {
                Gson().fromJson(
                    SchoolApi().get("http://www.devnvrsk.ru/api/Schedule/day?day=$day")
                        .bodyAsText(), object : TypeToken<List<Lesson>>() {}.type
                )
            }

            _state.value = true
        }
    }
}