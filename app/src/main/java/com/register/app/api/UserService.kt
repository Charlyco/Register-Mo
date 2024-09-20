package com.register.app.api

import com.register.app.dto.AuthResponse
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.FaqWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.MemberDetailWrapper
import com.register.app.dto.SignUpModel
import com.register.app.dto.UpdateUserResponse
import com.register.app.model.Faq
import com.register.app.model.Member
import com.register.app.model.PrivacyPolicyResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @POST("auth-service/api/v1/auth/member/signup")
    fun signUp(@Body signUpModel: SignUpModel): Call<AuthResponseWrapper>

    @GET("auth-service/api/v1/auth/member/login")
    fun signIn(@Query("emailAddress") emailAddress: String, @Query("password") password: String): Call<AuthResponseWrapper>

    @POST("auth-service/api/v1/user/group/members")
    fun getAllMembersForGroup(@Body memberEmail: List<String>): Call<MemberDetailWrapper?>

    @GET("auth-service/api/v1/user/member/email")
    fun getMemberDetails(@Query("emailAddress") emailAddress: String): Call<Member?>

    @POST("messaging-service/api/otp/email")
    fun sendOtp(@Query("emailAddress") emailAddress: String): Call<GenericResponse>

    @POST("messaging-service/api/otp/verify")
    fun verifyOtp(@Query("otp") otp: Int, @Query("emailAddress") emailAddress: String): Call<GenericResponse>

    @Multipart
    @POST("event-service/api/v1/event/image/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ImageUploadResponse>
    @PUT("auth-service/api/v1/user/member/{memberId}/update")
    fun updateUserData(@Path("memberId") memberId: Int, @Body updateData: Member): Call<UpdateUserResponse>
    @POST("auth-service/api/v1/auth/refreshToken")
    fun getRefreshToken(@Header("refreshToken") refreshToken: String): Call<AuthResponseWrapper>
    @GET("auth-service/api/v1/user/member/email")
    fun reloadUserData(@Query("emailAddress") emailAddress: String?): Call<Member>
    @GET("company-service/api/v1/faq")
    fun getFaqList(): Call<FaqWrapper>
    @GET("auth-service/api/v1/auth/check")
    fun checkEmailAndPhone(@Query("email") email: String, @Query("phone") phone: String): Call<GenericResponse>
    @PUT("auth-service/api/v1/auth/resetPassword")
    fun resetPassword(@Query("email") email: String?,
                      @Query("password") password: String): Call<GenericResponse>
    @GET("company-service/api/v1/privacy")
    fun getPrivacyStatement(): Call<PrivacyPolicyResponse>
}
