package com.nvrskdev.myschool.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDataStore(private val context: Context) {

    companion object {
        // At the top level of your kotlin file:
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_ROLE_KEY = intPreferencesKey("user_role")
    }

    suspend fun saveUserRole(userRole: Int) {
        context.dataStore.edit {
            it[USER_ROLE_KEY] = userRole
        }
    }

    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit {
            it[USER_ID_KEY] = userId
        }
    }

    val userId: Flow<Int> = context.dataStore.data
        .map {
            it[USER_ID_KEY] ?: -1
        }

    val userRole: Flow<Int> = context.dataStore.data
        .map {
            it[USER_ROLE_KEY] ?: -1
        }
}