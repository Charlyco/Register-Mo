package com.register.app.api

import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ChatService {
    @POST("auth-service/api/v1/user/firebase/verify")
    fun verifyFirebaseToken(@Body firebaseTokenModel: FirebaseTokenModel): Call<GenericResponse>
    @PUT("auth-service/api/v1/user/firebase/update")
    fun registerFirebaseToken(@Body firebaseTokenModel: FirebaseTokenModel): Call<GenericResponse>
}
