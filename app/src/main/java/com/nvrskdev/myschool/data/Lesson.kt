package com.nvrskdev.myschool.data

data class Lesson(
    val id: Int,
    val number: Int,
    val name: String,
    val dayOfWeek: Int,
    val room: Int,
    val teacherFullName: String
)