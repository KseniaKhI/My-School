package com.nvrskdev.myschool.ui.test

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.data.Answer
import com.nvrskdev.myschool.data.Question
import com.nvrskdev.myschool.datastore.AppDataStore
import com.nvrskdev.myschool.model.NewTestViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NewTestScreen(onNavigateToTest: () -> Unit) {
    var step = remember {
        mutableStateOf(0)
    }

    Column() {
        AnimatedContent(
            targetState = step,
            transitionSpec = { EnterTransition.None with ExitTransition.None }
        ) {
            if (step.value == 0) {
                OptionsScreen(step = step)
            } else {
                QuestionsScreen(onNavigateToTest = onNavigateToTest)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(model: NewTestViewModel = viewModel(), step: MutableState<Int>) {

    val test by model.test.collectAsState()

    val classNumbers = List(11) { it + 1 }

    val parallels = stringArrayResource(id = R.array.parallels)

    var classExpanded by remember {
        mutableStateOf(false)
    }

    var parallelExpanded by remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {

            //Text fields
            OutlinedTextField(
                value = test.name,
                onValueChange = { model.update(test.copy(name = it)) },
                label = { Text(text = stringResource(id = R.string.test_name_label)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = test.description,
                onValueChange = { model.update(test.copy(description = it)) },
                label = { Text(text = stringResource(id = R.string.test_description_label)) },
                maxLines = 14,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = if (test.totalQuestions == 0) "" else test.totalQuestions.toString(),
                onValueChange = { model.update(test.copy(totalQuestions = if (!it.isNullOrBlank()) it.toInt() else 0)) },
                label = { Text(text = stringResource(id = R.string.test_total_questions_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )

            Row(modifier = Modifier.padding(top = 16.dp)) {

                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = !classExpanded },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                ) {
                    OutlinedTextField(
                        value = test.classNumber.toString(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = classExpanded,
                        onDismissRequest = { classExpanded = false }) {
                        classNumbers.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it.toString()) },
                                onClick = {
                                    model.update(test.copy(classNumber = it))
                                    classExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = parallelExpanded,
                    onExpandedChange = { parallelExpanded = !parallelExpanded },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                ) {
                    OutlinedTextField(
                        value = parallels[test.parallel],
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parallelExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = parallelExpanded,
                        onDismissRequest = { parallelExpanded = false }) {
                        parallels.forEachIndexed { index, text ->
                            DropdownMenuItem(
                                text = { Text(text = text) },
                                onClick = {
                                    model.update(test.copy(parallel = index))
                                    parallelExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    //Check fields and move to next step
                    step.value = 1
                }) {
                Text(text = stringResource(id = R.string.next_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(model: NewTestViewModel = viewModel(), onNavigateToTest: () -> Unit) {
    val test by model.test.collectAsState()

    val context = LocalContext.current

    val appDataStore = AppDataStore(context)

    val teacherId by appDataStore.userId.collectAsState(initial = -1)

    var progress by remember {
        mutableStateOf(1f / test.totalQuestions)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    var questionText by remember {
        mutableStateOf("")
    }

    var answers = remember {
        mutableStateListOf<String>().apply { addAll(List(4) { "" }) }
    }

    val questions = remember {
        mutableStateListOf<Question>()
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {

        Column(modifier = Modifier.padding(bottom = 16.dp)) {

            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {

            //Text fields
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                maxLines = 14,
                label = { Text(text = stringResource(id = R.string.question_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            answers.forEachIndexed { index, _ ->
                OutlinedTextField(
                    value = answers[index],
                    onValueChange = { answers[index] = it },
                    maxLines = 4,
                    label = { Text(text = stringResource(id = if (index == 0) R.string.correct_answer_label else R.string.answer_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {

                    //Add new question to list
                    questions.add(
                        Question(
                            questionText,
                            answers.mapIndexed { index, text ->
                                Answer(
                                    text,
                                    index == 0
                                )
                            } as MutableList<Answer>))

                    //Clear text fields
                    questionText = ""
                    answers.replaceAll { "" }

                    if (progress < 1f) {
                        progress += (1f / test.totalQuestions)
                    } else {

                        //Change teacher id to new test
                        model.update(test.copy(teacherId = teacherId))

                        //Upload json data as file to server
                        //Show loading dialog
                        scope.launch {
                            delay(1000)

                            try {
                                SchoolApi().uploadTest(test, questions)
                            } catch (e: HttpRequestTimeoutException) {
                                //Show error if timeout is exceeded
                            } finally {
                                //Show upload completion message
                                //Or loading dialog close here

                                onNavigateToTest()
                            }
                        }
                    }
                }
            ) {
                Text(text = stringResource(id = if (progress < 1f) R.string.next_button else R.string.save_button))
            }
        }
    }
}