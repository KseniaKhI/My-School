package com.nvrskdev.myschool.ui.schedule

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.model.ScheduleViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(model: ScheduleViewModel = viewModel()) {

    val lessons by model.lessons.collectAsState()
    val state by model.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = !state) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {})
        }

        LazyColumn {
            items(lessons) {
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.weight(1f)
                            )

                            Text(text = it.number.toString(), fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.person_20px),
                                contentDescription = null
                            )

                            Text(
                                text = it.teacherFullName,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.door_front_20px),
                                contentDescription = null
                            )

                            Text(
                                text = it.room.toString(),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}