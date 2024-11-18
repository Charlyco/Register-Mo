package com.register.app.api

import com.register.app.dto.ActivityRate
import com.register.app.dto.AddContestantResponse
import com.register.app.dto.AdminUpdateResponse
import com.register.app.dto.BankDetail
import com.register.app.dto.BankDetailWrapper
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.Contestant
import com.register.app.dto.CreateGroupModel
import com.register.app.dto.Election
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupDetailWrapper
import com.register.app.dto.GroupNotificationWrapper
import com.register.app.dto.GroupUpdateDto
import com.register.app.dto.GroupsWrapper
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.JoinGroupDto
import com.register.app.dto.MembershipDtoWrapper
import com.register.app.dto.RemoveMemberModel
import com.register.app.dto.UpdateAdminDto
import com.register.app.dto.VoteDto
import com.register.app.model.Group
import com.register.app.model.MembershipRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    fun createNewGroup(@Body groupModel: CreateGroupModel): Call<GroupDetailWrapper?>
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
    @GET("group-service/api/v1/group/groupName")
    fun searchGroupByName(@Query("groupName") searchTag: String): Call<GroupsWrapper>
    @POST("group-service/api/v1/group/{groupId}/request")
    fun requestToJoinGroup(@Path("groupId") groupId: Int, @Body userInfo: JoinGroupDto): Call<GenericResponse>
    @PUT("group-service/api/v1/group/membershipRequest/approve")
    fun approveMembershipRequest(@Body membershipRequest: MembershipRequest): Call<GenericResponse>
    @GET("group-service/api/v1/group/{groupId}")
    fun getGroupDetails(@Path("groupId") groupId: Int?): Call<GroupDetailWrapper>

    @POST("election-service/api/v1/create")
    fun createElection(@Body election: Election): Call<GenericResponse>
    @GET("election-service/api/v1/{groupId}/all")
    fun getElections(@Path("groupId") groupId: Int?): Call<List<Election>>
    @POST("election-service/api/v1/vote")
    fun castVote(@Body voteModel: VoteDto): Call<GenericResponse>
    @PUT("election-service/api/v1/{electionId}/{contestantId}")
    fun removeContestant(@Path("contestantId") contestantId: Long?, @Path("electionId") electionId: Int?): Call<GenericResponse>
    @GET("election-service/api/v1/election/{electionId}")
    fun getElectionDetails(@Path("electionId") electionId: Int): Call<Election?>
    @PUT("election-service/api/v1/{electionId}/add")
    fun addContestant(@Body contestant: Contestant, @Path("electionId") electionId: Int): Call<AddContestantResponse>
    @GET("election-service/api/v1/{electionId}")
    fun checkIfUserHasVoted(@Path("electionId") electionId: Int?, @Query("voterName") user: String?,): Call<GenericResponse>
    @PUT("election-service/api/v1/{electionId}/start")
    fun startElection(@Path("electionId") electionId: Int?): Call<GenericResponse>
    @PUT("election-service/api/v1/{electionId}/end")
    fun endElection(@Path("electionId") electionId: Int?): Call<GenericResponse>
    @POST("group-service/api/v1/group/admin/new")
    fun makeAdmin(@Body updateAdminDto: UpdateAdminDto): Call<AdminUpdateResponse>
    @PUT("group-service/api/v1/group/admin/remove")
    fun removeAdmin(@Body updateAdminDto: UpdateAdminDto): Call<AdminUpdateResponse>
    @GET("messaging-service/api/notifications/{groupId}")
    fun getGroupNotifications(@Path("groupId") groupId: Int?): Call<GroupNotificationWrapper>
    @GET("group-service/api/v1/group/bankDetails/{groupId}")
    fun getBankDetails(@Path("groupId") groupId: Int): Call<BankDetailWrapper>
    @PUT("group-service/api/v1/group/bankDetails/{groupId}")
    fun updateBankDetail(@Body bankDetail: BankDetail, @Path("groupId") groupId: Int): Call<GenericResponse>
    @PUT("group-service/api/v1/group/membershipRequest/reject")
    fun rejectMembershipRequest(@Body selectedRequest: MembershipRequest): Call<GenericResponse>
    @DELETE("group-service/api/v1/group/{groupId}")
    fun deleteGroup(@Path("groupId") groupId: Int): Call<GenericResponse>
}
