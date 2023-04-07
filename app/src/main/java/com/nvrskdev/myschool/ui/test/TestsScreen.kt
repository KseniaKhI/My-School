package com.nvrskdev.myschool.ui.test

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.datastore.AppDataStore
import com.nvrskdev.myschool.model.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(
    onNavigateToNewTest: () -> Unit,
    onNavigateToJournal: (String) -> Unit,
    onNavigateToTestPassing: (String) -> Unit
) {
    val context = LocalContext.current

    val dataStore = AppDataStore(context)

    val userRole by dataStore.userRole.collectAsState(initial = -1)

    val model: TestViewModel = viewModel(
        factory = TestViewModel.provideFactory(
            context.applicationContext as Application,
            dataStore
        )
    )

    val tests by model.tests.collectAsState()
    val state by model.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column() {
            AnimatedVisibility(visible = !state) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) {}
                )
            }

            LazyColumn {
                items(tests) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            if (userRole < 2) {
                                onNavigateToJournal(Gson().toJson(it.journal))
                            } else {
                                onNavigateToTestPassing(Gson().toJson(it))
                            }
                        }
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = it.description,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.test_total_questions_label) + ": ${it.totalQuestions}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = stringResource(id = R.string.passed_people_label) + ": ${it.journal.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (userRole < 2) {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewTest,
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.add_24px), contentDescription = "")
                Text(text = stringResource(id = R.string.add_test_button))
            }
        }
    }
}