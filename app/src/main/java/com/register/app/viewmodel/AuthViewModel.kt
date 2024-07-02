package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.register.app.dto.AuthResponse
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.SignUpModel
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.repository.AuthRepository
import com.register.app.repository.ChatRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _progressLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressLiveData: LiveData<Boolean> = _progressLiveData
    private val _shouldResendOtp: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldResendOtp: LiveData<Boolean> = _shouldResendOtp
    private val _isOtpVerified: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOtpVerified: LiveData<Boolean> = _isOtpVerified
    private val _otpLiveData: MutableLiveData<String> = MutableLiveData("")
    val otpTimer: LiveData<String> = _otpLiveData
    private val _phoneNumber: MutableLiveData<String> = MutableLiveData("")
    val phoneNumber: LiveData<String> = _phoneNumber
    private val _errorLiveData: MutableLiveData<String?> = MutableLiveData("")
    val errorLiveData: LiveData<String?> = _errorLiveData
    private val _userLiveData: MutableLiveData<Member?> = MutableLiveData()
    val userLideData: LiveData<Member?> = _userLiveData
    private val _intendingMemberLiveData: MutableLiveData<Member?> = MutableLiveData()
    val intendedMemberLiveData: LiveData<Member?> = _intendingMemberLiveData

    suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        username: String,
        password: String,
        rePassword: String,
        address: String
    ): Boolean {
        if (firstName.isBlank()) {
            _errorLiveData.value = "First name cannot be blank"
        } else if (lastName.isBlank()) {
            _errorLiveData.value = "Last name cannot be blank"
        } else if (email.isBlank()) {
            _errorLiveData.value = "Email is empty or invalid"
        } else if (phone.isBlank()) {
            _errorLiveData.value = "Phone number cannot be blank"
        }else if (password.length < 6) {
            _errorLiveData.value = "Password must be at least 6 characters"
        }else if (rePassword != password) {
            _errorLiveData.value = "Password mismatch"
        } else {
            _progressLiveData.value = true
            val signUpModel = SignUpModel("$firstName $lastName", phone, email, username, password, address)
                val response =  authRepository.signUp(signUpModel)
            if (response?.status == true) {
                dataStoreManager.writeUserData(response.data?.member!!)
                _progressLiveData.value = false
                return true
            }else {
                _progressLiveData.value = false
                return false
            }
        }
        return false
    }

    fun setOtpValue(otp: String) {
        //Call repository method to verify otp
        //onSuccess, set _isOtpVerified to true
        //onError, set _errorLiveData to the value of the error
        _isOtpVerified.value = true
    }

    suspend fun sendOtp(phoneNumber: String): Boolean {
        //this is placeholder implementation
        countDownTimer(1)
        return true
    }

    private fun countDownTimer(duration: Int) { //A function that creates a countdown timer for OTP value expiration
        val initialTime = duration * 60
        var remainingTime = initialTime

        timer(period = 1000) {
            if (remainingTime > 0) {
                val formattedTime = formatTime(remainingTime)
                _otpLiveData.postValue(formattedTime)
                remainingTime--
            } else if (remainingTime == 0) {
                _shouldResendOtp.postValue(true)
                _otpLiveData.postValue("Time Elapsed!")
                cancel()
            }
        }
    }
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    suspend fun signIn(email: String, password: String): AuthResponseWrapper? {
        _progressLiveData.value = true
           val authResponse = authRepository.login(LoginUserModel(email, password))
        if (authResponse?.status == true) {
            dataStoreManager.writeUserData(authResponse.data?.member!!)
            dataStoreManager.writeTokenData(authResponse.data.authToken)
            updateFirebaseToken(dataStoreManager.readDeviceId(), dataStoreManager.readFirebaseToken())
            _progressLiveData.value = false
            _userLiveData.value = authResponse.data.member
            return authResponse
        }else{
            _progressLiveData.value = false
            return authResponse
        }
    }

    private suspend fun updateFirebaseToken(deviceId: String?, firebaseToken: String?) {
        Log.d("FCM", "Token: $firebaseToken")
        Log.d("FCM", "deviceId: $deviceId")
        val shouldUpdate = chatRepository.checkTokenWithDeviceId(deviceId!!, firebaseToken!!).data
        if (shouldUpdate == true) {
            chatRepository.updateFcmToken(FirebaseTokenModel(deviceId, firebaseToken))
        }
    }

    fun isUserAdmin(): Boolean {
        var role: String? = ""
        // Ca
        return true
        //return role
    }

    fun fetchMemberDetailsById(memberId: Int): Member {
        return Member(1,
            "Uche Egemba",
            "Urchman",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Member",
            "",
            "USER", listOf())
    }

    suspend fun getUserDetails() {
        _userLiveData.value = dataStoreManager.readUserData()!!
    }

    fun fetchMemberDetailsByEmail(memberEmail: String?): Member? {
        val member = Member(1,
            "Uche Egemba",
            "Urchman",
            "+2347037590923",
            "charlyco835@gmail.com",
            "12 Achuzilam Streen, Oppsite Divina Hospital, Nekede Owerri", "",
            "ACTIVE",
            "Member",
            "",
            "USER", listOf())
        return member
    }

    fun setSelectedMember(member: Member) {

    }

    suspend fun getMemberDetails(email: String) {
        _progressLiveData.value = true
        Log.d("Member", "fetching member details")
        val member = authRepository.getMemberDetails(email)
        _intendingMemberLiveData.value = member
        _progressLiveData.value = false
    }


}