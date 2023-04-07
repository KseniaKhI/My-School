package com.nvrskdev.myschool.api

import com.google.gson.Gson
import com.nvrskdev.myschool.data.Question
import com.nvrskdev.myschool.data.Test
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.util.*

class SchoolApi {
    suspend fun get(url: String) = HttpClient(CIO).use { it.get(url) }

    suspend fun addJournalEntry(testId: Int, studentId: Int, correctAnswers: Int = 0) {
        HttpClient(CIO) {
            install(HttpTimeout)
        }.use {
            it.post("http://www.devnvrsk.ru/api/Journal") {
                timeout {
                    requestTimeoutMillis = 5000
                }

                setBody(MultiPartFormDataContent(
                    formData {
                        append("TestId", testId)
                        append("StudentId", studentId)
                        append("CorrectAnswers", correctAnswers)
                    }
                ))
            }
        }
    }

    suspend fun uploadTest(test: Test, questions: MutableList<Question>) {
        HttpClient(CIO) {
            install(HttpTimeout)
        }.use {
            val response = it.post("http://www.devnvrsk.ru/api/Tests") {
                timeout {
                    requestTimeoutMillis = 10000
                }

                setBody(MultiPartFormDataContent(
                    formData {
                        append("Name", test.name)
                        append("Description", test.description)
                        append("TotalQuestions", test.totalQuestions)
                        append("ClassNumber", test.classNumber)
                        append("Parallel", test.parallel)
                        append("TeacherId", test.teacherId)
                        append("File", Gson().toJson(questions), Headers.build {
                            append(HttpHeaders.ContentType, "text/json")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${UUID.randomUUID()}.json\""
                            )
                        })
                    }
                ))
            }
        }
    }
}