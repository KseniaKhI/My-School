package com.nvrskdev.myschool.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.data.Journal

@Composable
fun JournalScreen(journalData: String) {

    val journal =
        Gson().fromJson<List<Journal>>(journalData, object : TypeToken<List<Journal>>() {}.type)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                Row(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                    Text(
                        text = stringResource(id = R.string.full_username_table_header),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(.7f)
                    )
                    Text(
                        text = stringResource(id = R.string.user_score_table_header),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(.3f)
                    )
                }
            }

            itemsIndexed(journal) { index, item ->
                val color =
                    if (index % 2 == 0) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiaryContainer

                Row(modifier = Modifier.background(color)) {
                    Text(
                        text = "${item.studentSurname} ${item.studentName}",
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(.7f)
                    )

                    Text(
                        text = item.correctAnswers.toString(),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(.3f)
                    )
                }
            }
        }
    }
}