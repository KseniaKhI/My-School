package com.nvrskdev.myschool.data

data class Question(
    val text: String,
    val answers: MutableList<Answer> = mutableListOf()
)
