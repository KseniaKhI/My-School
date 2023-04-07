package com.nvrskdev.myschool.ui.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.nvrskdev.myschool.R
import com.nvrskdev.myschool.api.SchoolApi
import com.nvrskdev.myschool.datastore.AppDataStore
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(hasEntered: MutableStateFlow<Boolean>) {
    val scope = rememberCoroutineScope()

    var code by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val dataStore = AppDataStore(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier.weight(1f)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(1f)
        ) {

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(text = stringResource(id = R.string.personal_code_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                scope.launch {
                    delay(1000)

                    val response =
                        SchoolApi().get("http://www.devnvrsk.ru/api/Auth/code?code=${code}")
                    if (response.status == HttpStatusCode.NotFound) {
                        //Show error if user not found
                        return@launch
                    }

                    var user = Gson().fromJson(response.bodyAsText(), object {
                        val id: Int = -1
                        val role: Int = -1
                    }::class.java)

                    //Save user role and id
                    dataStore.saveUserRole(user.role)
                    dataStore.saveUserId(user.id)

                    hasEntered.value = true
                }
            }, modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(id = R.string.sign_in_button))
            }
        }
    }
}