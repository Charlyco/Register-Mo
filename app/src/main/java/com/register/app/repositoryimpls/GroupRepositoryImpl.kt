package com.register.app.repositoryimpls

import com.register.app.api.ActivityService
import com.register.app.api.GroupService
import com.register.app.dto.ActivityRate
import com.register.app.dto.AddContestantResponse
import com.register.app.dto.AdminUpdateResponse
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.Contestant
import com.register.app.dto.CreateGroupModel
import com.register.app.dto.Election
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupDetailWrapper
import com.register.app.dto.GroupUpdateDto
import com.register.app.dto.GroupsWrapper
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.JoinGroupDto
import com.register.app.dto.MembershipDtoWrapper
import com.register.app.dto.RemoveMemberModel
import com.register.app.dto.UpdateAdminDto
import com.register.app.dto.VoteDto
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.model.MembershipRequest
import com.register.app.repository.GroupRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GroupRepositoryImpl @Inject constructor(
    private val groupService: GroupService,
    private val activityService: ActivityService
): GroupRepository {

    override suspend fun getAllGroupsForUser(groupIds: List<Int>?): List<Group>? {
        return suspendCoroutine { continuation ->
            val call = groupService.getAllGroupsForUser(groupIds)
            call.enqueue(object : Callback<List<Group>?> {
                override fun onResponse(
                    call: Call<List<Group>?>,
                    response: Response<List<Group>?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume( null)
                            }
                            500 -> continuation.resume( null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Group>?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getAllActivitiesForGroup(groupId: Int): List<Event>? {
        return suspendCoroutine { continuation ->
            val call = activityService.getAllActivitiesForGroup(groupId)
            call.enqueue(object : Callback<List<Event>?> {
                override fun onResponse(
                    call: Call<List<Event>?>,
                    response: Response<List<Event>?>
                ) {
                    if (response.isSuccessful){
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(null)
                            }
                            500 -> continuation.resume(  null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Event>?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun createNewGroup(groupModel: CreateGroupModel): Group? {
        return suspendCoroutine { continuation ->
            val call = groupService.createNewGroup(groupModel)
            call.enqueue(object : Callback<Group> {
                override fun onResponse(call: Call<Group>, response: Response<Group>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(null)
                            }
                            500 -> continuation.resume(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Group>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getMemberActivityRate(
        membershipId: String?,
        dateJoined: String?,
        groupId: Int
    ): ActivityRate {
        return suspendCoroutine { continuation ->
            val call = groupService.getMemberActivityRate(membershipId, dateJoined, groupId)
            call.enqueue(object : Callback<ActivityRate> {
                override fun onResponse(call: Call<ActivityRate>, response: Response<ActivityRate>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(ActivityRate("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(ActivityRate("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<ActivityRate>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun updateGroup(groupId: Int, group: GroupUpdateDto) : GenericResponse?{
        return suspendCoroutine { continuation ->
            val call = groupService.updateGroup(groupId, group)
            call.enqueue(object : Callback<GenericResponse?> {
                override fun onResponse(
                    call: Call<GenericResponse?>,
                    response: Response<GenericResponse?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun addMemberToGroup(groupId: Int?, emailAddress: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.addMemberToGroup(groupId, emailAddress)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun submitEvidenceOfPayment(): GenericResponse {
        return GenericResponse("", false, null)
    }

    override suspend fun uploadImage(image: RequestBody, name: String): ImageUploadResponse {
        return suspendCoroutine { continuation ->
            val file = MultipartBody.Part.createFormData("file", name, image)
            val call = groupService.uploadImage(file)
            call.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(
                    call: Call<ImageUploadResponse>,
                    response: Response<ImageUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(ImageUploadResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(ImageUploadResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun changeMemberStatus(
        membershipId: String,
        changeMemberStatusDto: ChangeMemberStatusDto
    ): MembershipDtoWrapper {
        return suspendCoroutine { continuation ->
            val call = groupService.changeMemberStatus(membershipId, changeMemberStatusDto)
            call.enqueue(object : Callback<MembershipDtoWrapper> {
                override fun onResponse(
                    call: Call<MembershipDtoWrapper>,
                    response: Response<MembershipDtoWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(MembershipDtoWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(MembershipDtoWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<MembershipDtoWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun expelMember(removeMemberModel: RemoveMemberModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.expelMember(removeMemberModel)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getGroupDetails(groupId: Int?): GroupDetailWrapper? {
        return suspendCoroutine { continuation ->
            val call = groupService.getGroupDetails(groupId)
            call.enqueue(object : Callback<GroupDetailWrapper> {
                override fun onResponse(
                    call: Call<GroupDetailWrapper>,
                    response: Response<GroupDetailWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GroupDetailWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GroupDetailWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GroupDetailWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun searchGroupByName(searchTag: String): GroupsWrapper? {
        return suspendCoroutine { continuation ->
            val call = groupService.searchGroupByName(searchTag)
            call.enqueue(object : Callback<GroupsWrapper?> {
                override fun onResponse(
                    call: Call<GroupsWrapper?>,
                    response: Response<GroupsWrapper?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GroupsWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GroupsWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GroupsWrapper?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun requestToJoinGroup(
        groupId: Int,
        userInfo: JoinGroupDto
    ): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.requestToJoinGroup(groupId, userInfo)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun approveMembershipRequest(membershipRequest: MembershipRequest): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.approveMembershipRequest(membershipRequest)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun createElection(election: Election): GenericResponse {
        return  suspendCoroutine { continuation ->
            val call = groupService.createElection(election)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getGroupElections(groupId: Int?): List<Election>? {
        return suspendCoroutine { continuation ->
            val call = groupService.getElections(groupId)
            call.enqueue(object : Callback<List<Election>?> {
                override fun onResponse(
                    call: Call<List<Election>?>,
                    response: Response<List<Election>?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume( null)
                            }
                            500 -> continuation.resume(  null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Election>?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun castVote(voteModel: VoteDto): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.castVote(voteModel)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun removeContestant(contestantId: Long?, electionId: Int?): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.removeContestant(contestantId, electionId)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getElectionDetails(electionId: Int): Election? {
        return suspendCoroutine { continuation ->
            val call = groupService.getElectionDetails(electionId)
            call.enqueue(object : Callback<Election?> {
                override fun onResponse(call: Call<Election?>, response: Response<Election?>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume( null)
                            }
                            500 -> continuation.resume( null)
                        }
                    }
                }

                override fun onFailure(call: Call<Election?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun addContestant(
        contestant: Contestant,
        electionId: Int
    ): AddContestantResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.addContestant(contestant, electionId)
            call.enqueue(object : Callback<AddContestantResponse> {
                override fun onResponse(
                    call: Call<AddContestantResponse>,
                    response: Response<AddContestantResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(AddContestantResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(AddContestantResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<AddContestantResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun checkIfUserHasVoted(user: String?, electionId: Int?): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.checkIfUserHasVoted(electionId, user)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun startElection(electionId: Int?): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.startElection(electionId)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun endElection(electionId: Int?): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.endElection(electionId)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun makeAdmin(updateAdminDto: UpdateAdminDto): AdminUpdateResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.makeAdmin(updateAdminDto)
            call.enqueue(object : Callback<AdminUpdateResponse> {
                override fun onResponse(
                    call: Call<AdminUpdateResponse>,
                    response: Response<AdminUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(
                                    AdminUpdateResponse(
                                        "Invalid Credentials",
                                        false,
                                        null
                                    )
                                )
                            }

                            500 -> continuation.resume(
                                AdminUpdateResponse(
                                    "Please check Internet connection and try again",
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<AdminUpdateResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun removeAdmin(updateAdminDto: UpdateAdminDto): AdminUpdateResponse {
        return suspendCoroutine { continuation ->
            val call = groupService.removeAdmin(updateAdminDto)
            call.enqueue(object : Callback<AdminUpdateResponse> {
                override fun onResponse(
                    call: Call<AdminUpdateResponse>,
                    response: Response<AdminUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(
                                    AdminUpdateResponse(
                                        "Invalid Credentials",
                                        false,
                                        null
                                    )
                                )
                            }

                            500 -> continuation.resume(
                                AdminUpdateResponse(
                                    "Please check Internet connection and try again",
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<AdminUpdateResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }
}
