package com.register.app.api

import com.register.app.dto.ActivityRate
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.CreateEventModel
import com.register.app.dto.CreateGroupModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupUpdateDto
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.MembershipDtoWrapper
import com.register.app.dto.RemoveMemberModel
import com.register.app.model.Group
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupService {
    @POST("group-service/api/v1/group/all")
    fun getAllGroupsForUser(@Body groupIds: List<Int>?): Call<List<Group>?>
    @POST("group-service/api/v1/group")
    fun createNewGroup(@Body groupModel: CreateGroupModel): Call<Group>
    @GET("event-service/api/v1/event/event/member/{membershipId}")
    fun getMemberActivityRate(@Path("membershipId") membershipId: String?, @Query("date") dateJoined: String?, @Query("groupId") groupId: Int): Call<ActivityRate>
    @PUT("group-service/api/v1/group/{groupId}")
    fun updateGroup(@Path("groupId") groupId: Int, @Body group: GroupUpdateDto): Call<GenericResponse?>
    @PUT("group-service/api/v1/group/{groupId}/add")
    fun addMemberToGroup(@Path("groupId") groupId: Int?, @Query("emailAddress") emailAddress: String): Call<GenericResponse>

    @Multipart
    @POST("event-service/api/v1/event/image/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ImageUploadResponse>

    @PUT("group-service/api/v1/group/member/{membershipId}/status")
    fun changeMemberStatus(
        @Path("membershipId") membershipId: String,
        @Body changeMemberStatusDto: ChangeMemberStatusDto
    ): Call<MembershipDtoWrapper>

    @PUT("group-service/api/v1/group/member/remove")
    fun expelMember(@Body removeMemberModel: RemoveMemberModel): Call<GenericResponse>
}
