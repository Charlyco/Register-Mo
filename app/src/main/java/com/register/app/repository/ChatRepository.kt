package com.register.app.repository

import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse

interface ChatRepository {
    suspend fun checkTokenWithDeviceId(deviceId: String, token: String): GenericResponse
    suspend fun updateFcmToken(firebaseTokenModel: FirebaseTokenModel): GenericResponse

}
