package com.nvrskdev.myschool.ui.test

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.data.Test
import com.nvrskdev.myschool.datastore.AppDataStore
import com.nvrskdev.myschool.model.QuestionViewModel
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch

@Composable
fun TestPassingScreen(testData: String, onNavigateToTest: () -> Unit) {
    val test = Gson().fromJson(testData, Test::class.java)

    val context = LocalContext.current

    val appDataStore = AppDataStore(context)

    val userId by appDataStore.userId.collectAsState(initial = -1)

    val model: QuestionViewModel = viewModel(
        factory = QuestionViewModel.provideFactory(
            context.applicationContext as Application,
            test.testPath
        )
    )

    val questions by model.questions.collectAsState()
    val state by model.state.collectAsState()

    var currentQuestion by remember {
        mutableStateOf(0)
    }

    var selectedAnswer by remember {
        mutableStateOf("")
    }

    var correctAnswers by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {

        SchoolApi().addJournalEntry(test.id, userId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = !state) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {})
        }

        if (state) {
            if (selectedAnswer.isNullOrBlank()) {
                selectedAnswer = questions[currentQuestion].answers[0].text
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = questions[currentQuestion].text,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start
                )

                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .weight(1f)
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {

                    questions[currentQuestion].answers.forEach {
                        Row(verticalAlignment = CenterVertically) {
                            RadioButton(
                                selected = (it.text == selectedAnswer),
                                onClick = { selectedAnswer = it.text })

                            Text(text = it.text)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = BottomEnd) {
                    Button(onClick = {
                        if (questions[currentQuestion].answers.find { answer -> answer.isCorrect }?.text == selectedAnswer) {
                            correctAnswers++
                        }

                        if (currentQuestion < test.totalQuestions - 1) {
                            currentQuestion++

                            selectedAnswer = ""
                        } else {
                            //Save result to server
                            scope.launch {
                                try {
                                    println(correctAnswers)
                                    SchoolApi().addJournalEntry(test.id, userId, correctAnswers)
                                } catch (e: HttpRequestTimeoutException) {

                                } finally {
                                    onNavigateToTest()
                                }
                            }
                        }
                    }) {
                        Text(
                            text = stringResource(id = if (currentQuestion < test.totalQuestions - 1) R.string.next_button else R.string.save_button)
                        )
                    }
                }
            }
        }
    }
}