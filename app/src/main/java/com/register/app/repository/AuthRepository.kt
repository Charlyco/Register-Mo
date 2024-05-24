package com.register.app.repository

import com.register.app.dto.AuthResponse
import com.register.app.dto.GenericResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.SendOtpModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.VerifyOtpModel

interface AuthRepository {
    suspend fun login(loginData: LoginUserModel): AuthResponse?
    suspend fun signUp(userDetail: SignUpModel): AuthResponse?
    suspend fun sendOtp(senOtpModel: SendOtpModel): GenericResponse
    suspend fun verifyOtp(verifyOtpModel: VerifyOtpModel): GenericResponse
}
