package com.nvrskdev.myschool.ui.teacher

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.data.Teacher

@Composable
fun TeacherDetailsScreen(teacherData: String) {

    val context = LocalContext.current

    val teacher = Gson().fromJson(teacherData, Teacher::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Row() {
            AsyncImage(
                model = "http://devnvrsk.ru/images/avatars/${teacher.avatarPath}",
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
                    text = "${teacher.surname} ${teacher.name} ${teacher.patronymic}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = teacher.post,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_month_20px),
                        contentDescription = null
                    )

                    Text(
                        text = teacher.formatDate(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                }
            }
        }

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(
                onClick = {
                    val uri = Uri.parse("tel:" + teacher.phone)
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(end = 4.dp)
                    .weight(1f)
            ) {
                Text(text = stringResource(id = R.string.call_button))
            }

            Button(
                onClick = {
                    val uri = Uri.parse("smsto:" + teacher.phone)
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(start = 4.dp)
                    .weight(1f)
            ) {
                Text(text = stringResource(id = R.string.send_message_button))
            }
        }

        LazyRow(modifier = Modifier.padding(top = 16.dp)) {
            items(teacher.achievements) {
                AsyncImage(
                    model = "http://devnvrsk.ru/images/achievements/${it.imagePath}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(146.dp)
                        .height(146.dp)
                        .padding(start = 8.dp, end = 8.dp)
                        .clickable {
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                setDataAndType(
                                    Uri.parse("http://devnvrsk.ru/images/achievements/${it.imagePath}"),
                                    "image/*"
                                )
                            }

                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}