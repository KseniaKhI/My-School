package com.nvrskdev.myschool.data

data class Test(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val totalQuestions: Int = 0,
    val classNumber: Int = 1,
    val parallel: Int = 0,
    val testPath: String = "",
    val teacherId: Int = -1,
    val journal: MutableList<Journal> = mutableListOf()
)
