package com.nvrskdev.myschool.ui.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.data.NewsStorage.allNews

@Composable
fun NewsScreen() {
    LazyColumn {
        items(allNews) {

            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(text = it.author, style = MaterialTheme.typography.titleMedium)

                    Text(
                        text = it.pubDate,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(text = it.title, modifier = Modifier.padding(top = 16.dp))

                    Image(
                        painter = painterResource(id = it.attachments[0]),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clip(Shapes().medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}