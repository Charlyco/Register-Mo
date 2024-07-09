package com.register.app.repository

import com.register.app.dto.AuthResponse
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.MemberDetailWrapper
import com.register.app.dto.SendOtpModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.VerifyOtpModel
import com.register.app.model.Member

interface AuthRepository {
    suspend fun login(loginData: LoginUserModel): AuthResponseWrapper?
    suspend fun signUp(userDetail: SignUpModel): AuthResponseWrapper?
    suspend fun sendOtp(email: String): GenericResponse?
    suspend fun verifyOtp(otp: Int, emailAddress: String): GenericResponse
    suspend fun getAllMembersForGroup(memberEmail: List<String>): MemberDetailWrapper?
    suspend fun getMemberDetails(email: String): Member?
}
