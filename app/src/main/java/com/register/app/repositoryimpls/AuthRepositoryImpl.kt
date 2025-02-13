package com.register.app.repositoryimpls

import com.register.app.api.UserService
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.FaqWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.MemberDetailWrapper
import com.register.app.dto.SignUpModel
import com.register.app.dto.UpdateUserResponse
import com.register.app.model.Member
import com.register.app.model.PrivacyPolicyResponse
import com.register.app.repository.AuthRepository
import com.register.app.util.ExceptionHandler
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(private val userService: UserService): AuthRepository {
    override suspend fun login(loginData: LoginUserModel): AuthResponseWrapper? {
        return suspendCoroutine { continuation ->
            val call = userService.signIn(loginData.email, loginData.password)
            call.enqueue(object : Callback<AuthResponseWrapper> {
                override fun onResponse(
                    call: Call<AuthResponseWrapper>,
                    response: Response<AuthResponseWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        continuation.resume(AuthResponseWrapper(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<AuthResponseWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun signUp(userDetail: SignUpModel): AuthResponseWrapper? {
        return suspendCoroutine { continuation ->
            val call = userService.signUp(userDetail)
            call.enqueue(object : Callback<AuthResponseWrapper> {
                override fun onResponse(
                    call: Call<AuthResponseWrapper>,
                    response: Response<AuthResponseWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        continuation.resume(AuthResponseWrapper(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<AuthResponseWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun sendOtp(email: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = userService.sendOtp(email)
            call.enqueue(object : Callback<GenericResponse>{
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        continuation.resume(GenericResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun verifyOtp(otp: Int, emailAddress: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = userService.verifyOtp(otp, emailAddress)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        continuation.resume(GenericResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getAllMembersForGroup(memberEmail: List<String>): MemberDetailWrapper? {
        return suspendCoroutine { continuation ->
            val call = userService.getAllMembersForGroup(memberEmail)
            call.enqueue(object : Callback<MemberDetailWrapper?> {
                override fun onResponse(
                    call: Call<MemberDetailWrapper?>,
                    response: Response<MemberDetailWrapper?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        continuation.resume(MemberDetailWrapper(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<MemberDetailWrapper?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getMemberDetailsByEmail(emailAddress: String): Member? {
        return suspendCoroutine { continuation ->
            val call = userService.getMemberDetailsByEmail(emailAddress)
            call.enqueue(object : Callback<Member?> {
                override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<Member?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun uploadImage(
        requestBody: RequestBody,
        fileNameFromUri: String
    ): ImageUploadResponse {
        return suspendCoroutine { continuation ->
            val file = MultipartBody.Part.createFormData("file", fileNameFromUri, requestBody)
            val call = userService.uploadImage(file)
            call.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(
                    call: Call<ImageUploadResponse>,
                    response: Response<ImageUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                       continuation.resume(ImageUploadResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun updateUserData(memberId: Int, updateData: Member): UpdateUserResponse {
        return suspendCoroutine { continuation ->
            val call = userService.updateUserData(memberId, updateData)
            call.enqueue(object : Callback<UpdateUserResponse> {
                override fun onResponse(
                    call: Call<UpdateUserResponse>,
                    response: Response<UpdateUserResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                         continuation.resume(UpdateUserResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getRefreshToken(refreshToken: String): AuthResponseWrapper {
        return suspendCoroutine { continuation ->
            val call = userService.getRefreshToken(refreshToken)
            call.enqueue(object : Callback<AuthResponseWrapper> {
                override fun onResponse(
                    call: Call<AuthResponseWrapper>,
                    response: Response<AuthResponseWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        continuation.resume(AuthResponseWrapper(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<AuthResponseWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun reloadUserData(emailAddress: String?): Member? {
        return suspendCoroutine { continuation ->
            val call = userService.reloadUserData(emailAddress)
            call.enqueue(object : Callback<Member?> {
                override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        continuation.resume(  null)
                    }
                }

                override fun onFailure(call: Call<Member?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getFaqList(): FaqWrapper {
        return suspendCoroutine { continuation ->
            val call = userService.getFaqList()
            call.enqueue(object : Callback<FaqWrapper> {
                override fun onResponse(call: Call<FaqWrapper>, response: Response<FaqWrapper>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        continuation.resume(FaqWrapper(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<FaqWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun checkEmailAndPhone(email: String, phone: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = userService.checkEmailAndPhone(email, phone)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        continuation.resume(GenericResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun resetPassword(
        email: String?,
        password: String
    ): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = userService.resetPassword(email, password)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        continuation.resume(GenericResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getPrivacyStatement(): PrivacyPolicyResponse? {
        return suspendCoroutine { continuation ->
            val call = userService.getPrivacyStatement()
            call.enqueue(object: Callback<PrivacyPolicyResponse> {
                override fun onResponse(
                    call: Call<PrivacyPolicyResponse>,
                    response: Response<PrivacyPolicyResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    } else {
                        continuation.resume(PrivacyPolicyResponse(response.message(), false, null))
                    }
                }

                override fun onFailure(call: Call<PrivacyPolicyResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getMemberDetailsByPhone(phoneNumber: String): Member? {
        return suspendCoroutine { continuation ->
            val call = userService.getMemberDetailsByPhone(phoneNumber)
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else {
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }
}
