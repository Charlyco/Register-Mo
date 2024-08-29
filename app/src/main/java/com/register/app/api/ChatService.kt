package com.register.app.api

import com.register.app.dto.DirectChatMessages
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.ForumMessages
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {
    @POST("auth-service/api/v1/user/firebase/verify")
    fun verifyFirebaseToken(@Body firebaseTokenModel: FirebaseTokenModel): Call<GenericResponse>
    @PUT("auth-service/api/v1/user/firebase/update")
    fun registerFirebaseToken(@Body firebaseTokenModel: FirebaseTokenModel): Call<GenericResponse>
    @GET("chat-service/api/chats/{groupId}")
    fun getForumMessages(@Path("groupId") groupId: Int): Call<ForumMessages?>
    @GET("chat-service/api/chat/user/{recipientId}")
    fun getUserChatMessages(@Path("recipientId") recipientId: String, @Query("senderId") senderId: String): Call<DirectChatMessages?>
}
