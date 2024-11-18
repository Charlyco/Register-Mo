package com.register.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.register.app.dto.RefreshToken
import com.register.app.model.Member
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class DataStoreManager(private val applicationContext: Context) {
    val json = Json { ignoreUnknownKeys = true }

    private object PreferencesKeys {
        val tokenKey = stringPreferencesKey("token")
        val refreshTokenKey = stringPreferencesKey("refreshToken")
        val user = stringPreferencesKey("user")
        val loginTime = stringPreferencesKey("role")
        val firebaseToken = stringPreferencesKey("firebase")
        val contactPermission = stringPreferencesKey("contactPermission")
        val deviceId = stringPreferencesKey("deviceId")
        val shouldRequestLogin = booleanPreferencesKey("loginType")
    }

    // Singleton pattern for DataStoreManager
    companion object {
        private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_datastore")
        private val Context.refreshTokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "refresh_token_datastore")
        private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")
        private val Context.loginTimeDataStore: DataStore<Preferences> by preferencesDataStore(name = "role_datastore")
        private val Context.firebaseDataStore: DataStore<Preferences> by preferencesDataStore(name = "firebase_datastore")
        private val Context.contactPermission: DataStore<Preferences> by preferencesDataStore(name = "contact_perm_datastore")
        private val Context.deviceIdDataStore: DataStore<Preferences> by preferencesDataStore(name= "device_id_datastore")
        private val Context.loginTypeDataStore: DataStore<Preferences> by preferencesDataStore(name = "login_type_datastore")

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
        return applicationContext.userDataStore.data.map { member ->
            member[PreferencesKeys.user]?.let { json.decodeFromString(Member.serializer(), it) } }.firstOrNull()
    }

    suspend fun writeUserData(user: Member) {
        applicationContext.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.user] = json.encodeToString(Member.serializer(), user)
        }
    }

    suspend fun readLoginTime() : LocalDateTime? {
        return applicationContext.loginTimeDataStore.data.map { time ->
            time[PreferencesKeys.loginTime]?.let { LocalDateTime.parse(it) }}.firstOrNull()
    }

    suspend fun writeLoginTime(time: LocalDateTime) {
        applicationContext.loginTimeDataStore.edit { preferences ->
            preferences[PreferencesKeys.loginTime] = time.toString()
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

    suspend fun writeRefreshTokenData(refreshToken: RefreshToken) {
        applicationContext.refreshTokenDataStore.edit { preferences ->
            preferences[PreferencesKeys.refreshTokenKey] = json.encodeToString(RefreshToken.serializer(), refreshToken)
        }
    }

    suspend fun readRefreshToken(): RefreshToken? {
        return applicationContext.refreshTokenDataStore.data.map { preferences ->
            preferences[PreferencesKeys.refreshTokenKey]?.let {
            json.decodeFromString(RefreshToken.serializer(), it)
        } }.firstOrNull()
    }

    suspend fun readLoginType(): Boolean? {
        return applicationContext.loginTypeDataStore.data.map { it[PreferencesKeys.shouldRequestLogin] }.firstOrNull()
    }

    suspend fun writeLoginType(shouldRequestLogin: Boolean) {
        applicationContext.loginTypeDataStore.edit { preferences ->
            preferences[PreferencesKeys.shouldRequestLogin] = shouldRequestLogin
        }
    }
}