package com.register.app.repository

import com.register.app.dto.ActivityRate
import com.register.app.dto.AddContestantResponse
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
import com.register.app.dto.VoteDto
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.model.MembershipRequest
import okhttp3.RequestBody

interface GroupRepository {
    suspend fun getAllGroupsForUser(groupIds: List<Int>?): List<Group>?
    suspend fun getAllActivitiesForGroup(groupId: Int): List<Event>?
    suspend fun createNewGroup(groupModel: CreateGroupModel): Group
    suspend fun getMemberActivityRate(membershipId: String?, dateJoined: String?, groupId: Int): ActivityRate
    suspend fun updateGroup(groupId: Int, group: GroupUpdateDto): GenericResponse?
    suspend fun addMemberToGroup(groupId: Int?, emailAddress: String): GenericResponse
    suspend fun submitEvidenceOfPayment(): GenericResponse
    suspend fun uploadImage(image: RequestBody, name: String): ImageUploadResponse
    suspend fun changeMemberStatus(membershipId: String, changeMemberStatusDto: ChangeMemberStatusDto): MembershipDtoWrapper
    suspend fun expelMember(removeMemberModel: RemoveMemberModel): GenericResponse
    suspend fun getGroupDetails(groupId: Int?): GroupDetailWrapper?
    suspend fun searchGroupByName(searchTag: String): GroupsWrapper?
    suspend fun requestToJoinGroup(groupId: Int, userInfo: JoinGroupDto): GenericResponse
    suspend fun approveMembershipRequest(membershipRequest: MembershipRequest): GenericResponse
    suspend fun createElection(election: Election): GenericResponse
    suspend fun getGroupElections(groupId: Int?): List<Election>?
    suspend fun castVote(voteModel: VoteDto): GenericResponse
    suspend fun removeContestant(contestantId: Long?, electionId: Int?): GenericResponse
    suspend fun getElectionDetails(electionId: Int): Election?
    suspend fun addContestant(contestant: Contestant, electionId: Int): AddContestantResponse
    suspend fun checkIfUserHasVoted(user: String?, electionId: Int?): GenericResponse
    suspend fun startElection(electionId: Int?): GenericResponse
    suspend fun endElection(electionId: Int?): GenericResponse
}
