package com.register.app.repositoryimpls

import com.register.app.dto.AuthResponse
import com.register.app.dto.GenericResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.SendOtpModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.VerifyOtpModel
import com.register.app.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(): AuthRepository {
    override suspend fun login(loginData: LoginUserModel): AuthResponse? {
      return null
    }

    override suspend fun signUp(userDetail: SignUpModel): AuthResponse? {
        return null
    }

    override suspend fun sendOtp(senOtpModel: SendOtpModel): GenericResponse {
        TODO("Not yet implemented")
    }

    override suspend fun verifyOtp(verifyOtpModel: VerifyOtpModel): GenericResponse {
        TODO("Not yet implemented")
    }

}
