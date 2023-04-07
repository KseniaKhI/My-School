package com.nvrskdev.myschool.model

import androidx.lifecycle.ViewModel
import com.nvrskdev.myschool.data.Test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewTestViewModel : ViewModel() {
    private val _test: MutableStateFlow<Test> = MutableStateFlow(Test())
    val test = _test.asStateFlow()

    init {

    }

    fun update(newTest: Test) {
        _test.value = newTest
    }
}