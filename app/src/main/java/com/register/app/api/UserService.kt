package com.register.app.api

import com.register.app.dto.AuthResponse
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.SignUpModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("auth-service/api/v1/auth/member/signup")
    fun signUp(@Body signUpModel: SignUpModel): Call<AuthResponseWrapper>

    @GET("auth-service/api/v1/auth/member/login")
    fun signIn(@Query("emailAddress") emailAddress: String, @Query("password") password: String): Call<AuthResponseWrapper>
}
