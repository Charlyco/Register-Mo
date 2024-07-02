package com.register.app.repositoryimpls

import com.register.app.api.ChatService
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.repository.ChatRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatRepositoryImpl @Inject constructor(private val chatService: ChatService): ChatRepository {
    override suspend fun checkTokenWithDeviceId(deviceId: String, token: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = chatService.verifyFirebaseToken(FirebaseTokenModel(deviceId, token))
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun updateFcmToken(firebaseTokenModel: FirebaseTokenModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = chatService.registerFirebaseToken(firebaseTokenModel)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
