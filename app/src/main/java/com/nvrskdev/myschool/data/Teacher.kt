package com.nvrskdev.myschool.data

import java.text.DateFormat
import java.text.SimpleDateFormat

data class Teacher(
    val id: Int,
    val name: String,
    val surname: String,
    val patronymic: String,
    val post: String,
    val phone: String,
    val avatarPath: String,
    val dateOfBirth: String,
    val achievements: MutableList<Achievement> = mutableListOf()
) {
    fun formatDate(): String {
        val date = SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth)
        return DateFormat.getDateInstance().format(date)
    }
}
