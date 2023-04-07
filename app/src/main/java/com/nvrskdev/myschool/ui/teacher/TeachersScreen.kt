package com.nvrskdev.myschool.ui.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.nvrskdev.myschool.model.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachersScreen(
    model: TeacherViewModel = viewModel(),
    onNavigateToTeacherDetails: (String) -> Unit
) {
    val teachers by model.teachers.collectAsState()
    val state by model.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = !state) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {})
        }

        LazyColumn {
            items(teachers) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = Shapes().medium,
                    onClick = { onNavigateToTeacherDetails(Gson().toJson(it)) }
                ) {

                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        AsyncImage(
                            model = "http://devnvrsk.ru/images/avatars/${it.avatarPath}",
                            contentDescription = null,
                            modifier = Modifier
                                .width(146.dp)
                                .height(146.dp)
                                .clip(Shapes().medium),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        ) {

                            Text(
                                text = "${it.surname} ${it.name} ${it.patronymic}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = it.post,
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

//http://devnvrsk.ru/images/avatars/path