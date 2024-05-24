package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.register.app.dto.BankDetail
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.sync.Mutex
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val dataStoreManager: DataStoreManager): ViewModel(){
    private val _groupDetailLiveData: MutableLiveData<Group> = MutableLiveData()
    val groupDetailLiveData: LiveData<Group> = _groupDetailLiveData
    private val _bankDetails: MutableLiveData<BankDetail> = MutableLiveData()
    val bankDetails: LiveData<BankDetail> = _bankDetails
    private val _paymentEvidence: MutableLiveData<String?> = MutableLiveData()
    val paymentEvidence: LiveData<String?> = _paymentEvidence
    private val _groupListLiveDate: MutableLiveData<List<Group>> = MutableLiveData()
    val groupListLiveData: LiveData<List<Group>> = _groupListLiveDate
    val showCreateGroupSheet: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _groupAdminList: MutableLiveData<List<Member>> = MutableLiveData()
    val groupAdminList: LiveData<List<Member>> = _groupAdminList

    init {
        getAllGroupsForUser()
    }

    private fun getAllGroupsForUser() {
        //fetch groups from server

        val groups = listOf(
            Group(1, "IHS-2008", "2008 set of Isuikwuato High School",
                "charlyco@gmail.com", "+234-7037590923", "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",  LocalDateTime.now().toString(), listOf(), listOf(), listOf(), "", "", "" ),
            Group(1, "CMO St Patrick's Parish", "The Catholic Men Organization of St. Patrick's Parish Nekede, Owerri",
                "charlyco@gmail.com", "+234-7037590923", "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",  LocalDateTime.now().toString(), listOf(), listOf(), listOf(), "", "", "" ),

            )
        _groupListLiveDate.value = groups
    }

    fun getBankDetails() {
        val detail = BankDetail("Onuoha Chukwuemeka",
            "000999888777",
            "First Bank")
        _bankDetails.value = detail
    }

    fun setSelectedGroupDetail(group: Group) {
        _groupDetailLiveData.value = group
    }

    fun getIndividualAdminDetail() {
        TODO("Not yet implemented")
    }
}
