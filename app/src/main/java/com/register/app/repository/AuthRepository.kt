package com.register.app.repository

import com.register.app.dto.AuthResponse
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.FaqWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.MemberDetailWrapper
import com.register.app.dto.SendOtpModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.UpdateUserResponse
import com.register.app.dto.VerifyOtpModel
import com.register.app.model.Faq
import com.register.app.model.Member
import okhttp3.RequestBody

interface AuthRepository {
    suspend fun login(loginData: LoginUserModel): AuthResponseWrapper?
    suspend fun signUp(userDetail: SignUpModel): AuthResponseWrapper?
    suspend fun sendOtp(email: String): GenericResponse?
    suspend fun verifyOtp(otp: Int, emailAddress: String): GenericResponse
    suspend fun getAllMembersForGroup(memberEmail: List<String>): MemberDetailWrapper?
    suspend fun getMemberDetails(email: String): Member?
    suspend fun uploadImage(requestBody: RequestBody, fileNameFromUri: String): ImageUploadResponse
    suspend fun updateUserData(memberId: Int, updateData: Member): UpdateUserResponse
    suspend fun getRefreshToken(refreshToken: String): AuthResponseWrapper
    suspend fun reloadUserData(emailAddress: String?): Member?
    suspend fun getFaqList(): FaqWrapper
    suspend fun checkEmailAndPhone(email: String, phone: String): GenericResponse
    suspend fun resetPassword(email: String?, password: String): GenericResponse
}
