package com.register.app.repositoryimpls

import android.util.Log
import com.register.app.api.UserService
import com.register.app.dto.AuthResponse
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.MemberDetailWrapper
import com.register.app.dto.SendOtpModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.VerifyOtpModel
import com.register.app.model.Member
import com.register.app.repository.AuthRepository
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
                        val responseCode = response.code()
                        if (responseCode == 401) {
                            continuation.resume(AuthResponseWrapper("Invalid Credentials", false, null))
                        }
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
                    }
                }

                override fun onFailure(call: Call<MemberDetailWrapper?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getMemberDetails(emailAddress: String): Member? {
        return suspendCoroutine { continuation ->
            val call = userService.getMemberDetails(emailAddress)
            call.enqueue(object : Callback<Member?> {
                override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }
                }

                override fun onFailure(call: Call<Member?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}
