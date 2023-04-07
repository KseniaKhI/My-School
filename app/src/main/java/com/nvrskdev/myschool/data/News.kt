package com.nvrskdev.myschool.data

data class News(
    val title: String,
    val description: String,
    val pubDate: String,
    val author: String,
    val attachments: MutableList<Int> = mutableListOf()
)
