package com.register.app.repositoryimpls

import com.register.app.api.ActivityService
import com.register.app.api.GroupService
import com.register.app.dto.ActivityRate
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.CreateEventModel
import com.register.app.dto.CreateGroupModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupUpdateDto
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.MembershipDtoWrapper
import com.register.app.dto.RemoveMemberModel
import com.register.app.model.Event
import com.register.app.model.Group
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
                    }
                }

                override fun onFailure(call: Call<List<Event>?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun createNewGroup(groupModel: CreateGroupModel): Group {
        return suspendCoroutine { continuation ->
            val call = groupService.createNewGroup(groupModel)
            call.enqueue(object : Callback<Group> {
                override fun onResponse(call: Call<Group>, response: Response<Group>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
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
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getGroupDetails(groupId: Int?): Group? {
        return null
    }
}
