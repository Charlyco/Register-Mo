package com.register.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.register.app.model.Member
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataStoreManager(private val applicationContext: Context) {
    val json = Json { ignoreUnknownKeys = true }

    private object PreferencesKeys {
        val tokenKey = stringPreferencesKey("token")
        val user = stringPreferencesKey("user")
        val userRole = stringPreferencesKey("role")
        val firebaseToken = stringPreferencesKey("firebase")
        val contactPermission = stringPreferencesKey("contactPermission")
        val deviceId = stringPreferencesKey("deviceId")
        val userEmail = stringPreferencesKey("email")
    }

    // Singleton pattern for DataStoreManager
    companion object {
        private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_datastore")
        private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")
        private val Context.userRoleDataStore: DataStore<Preferences> by preferencesDataStore(name = "role_datastore")
        private val Context.firebaseDataStore: DataStore<Preferences> by preferencesDataStore(name = "firebase_datastore")
        private val Context.contactPermission: DataStore<Preferences> by preferencesDataStore(name = "contact_perm_datastore")
        private val Context.deviceIdDataStore: DataStore<Preferences> by preferencesDataStore(name= "device_id_datastore")
        private val Context.userEmailDataStore: DataStore<Preferences> by preferencesDataStore(name = "email_datastore")

        @Volatile
        private var instance: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return instance ?: synchronized(this) {
                instance ?: DataStoreManager(context.applicationContext).also { instance = it }
            }
        }
    }

    suspend fun readTokenData(): String? {
        return applicationContext.tokenDataStore.data.map { it[PreferencesKeys.tokenKey] }.firstOrNull()
    }

    suspend fun writeTokenData(token: String) {
        applicationContext.tokenDataStore.edit { preferences ->
            preferences[PreferencesKeys.tokenKey] = token
        }
    }

    suspend fun readUserData(): Member? {
        return applicationContext.userDataStore.data.map { member -> member[PreferencesKeys.user]?.let { json.decodeFromString(Member.serializer(), it) } }.firstOrNull()
    }

    suspend fun writeUserData(user: Member) {
        applicationContext.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.user] = json.encodeToString(Member.serializer(), user)
        }
    }

    suspend fun readUserRoleData() : String? {
        return applicationContext.userRoleDataStore.data.map {it[PreferencesKeys.userRole]}.firstOrNull()
    }

    suspend fun writeUserRoleData(role: String) {
        applicationContext.userRoleDataStore.edit { preferences ->
            preferences[PreferencesKeys.userRole] = role
        }
    }
    suspend fun readFirebaseToken(): String? {
        return applicationContext.firebaseDataStore.data.map { it[PreferencesKeys.firebaseToken] }.firstOrNull()
    }

    suspend fun writeFirebaseToken(token: String) {
        applicationContext.firebaseDataStore.edit { preferences ->
            preferences[PreferencesKeys.firebaseToken] = token
        }
    }

    suspend fun writeContactPermissionData(permission: String) {
        applicationContext.contactPermission.edit { preferences ->
            preferences[PreferencesKeys.contactPermission] = permission
        }
    }

    suspend fun readContactPermissionStatus(): String? {
        return applicationContext.contactPermission.data.map { it[PreferencesKeys.contactPermission] }.firstOrNull()
    }

    suspend fun writeDeviceId(deviceId: String) {
        applicationContext.deviceIdDataStore.edit { preferences ->
            preferences[PreferencesKeys.deviceId] = deviceId
        }
    }

    suspend fun readDeviceId() : String? {
        return applicationContext.deviceIdDataStore.data.map { it[PreferencesKeys.deviceId] }.firstOrNull()
    }

    suspend fun writeUserEmailData(email: String) {
        applicationContext.userEmailDataStore.edit { preferences ->
            preferences[PreferencesKeys.userEmail] = email
        }
    }

    suspend fun readUserEmailData(): String? {
        return applicationContext.userEmailDataStore.data.map { it[PreferencesKeys.userEmail] }.firstOrNull()
    }
}