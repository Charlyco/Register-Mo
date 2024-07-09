package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.LoginUserModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.VerifyOtpModel
import com.register.app.model.Member
import com.register.app.repository.AuthRepository
import com.register.app.repository.ChatRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _otpTimer: MutableLiveData<String> = MutableLiveData("")
    val otpTimer: LiveData<String> = _otpTimer
    private val _otpLiveData: MutableLiveData<Int> = MutableLiveData()
    val otpLiveData: LiveData<Int> = _otpLiveData
    private val _phoneNumber: MutableLiveData<String> = MutableLiveData("")
    val phoneNumber: LiveData<String> = _phoneNumber
    private val _errorLiveData: MutableLiveData<String?> = MutableLiveData("")
    val errorLiveData: LiveData<String?> = _errorLiveData
    private val _userLiveData: MutableLiveData<Member?> = MutableLiveData()
    val userLideData: LiveData<Member?> = _userLiveData
    private val _intendingMemberLiveData: MutableLiveData<Member?> = MutableLiveData()
    val intendedMemberLiveData: LiveData<Member?> = _intendingMemberLiveData
    private val _signUpModelLiveData: MutableLiveData<SignUpModel> = MutableLiveData()
    val signUpModelLiveData: LiveData<SignUpModel> = _signUpModelLiveData

    suspend fun signUp(
        username: String,
        password: String,
        rePassword: String,
        address: String
    ): Boolean {
        if (password.length < 6) {
            _errorLiveData.value = "Password must be at least 6 characters"
        }else if (rePassword != password) {
            _errorLiveData.value = "Password mismatch"
        } else if (username.isBlank()) {
            _errorLiveData.value = "Username cannot be blank"
        }else {
            _progressLiveData.value = true
            val signUpModel = signUpModelLiveData.value
            signUpModel?.username = username
            signUpModel?.address = address
            signUpModel?.password = password
                val response =  authRepository.signUp(signUpModel!!)
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

    suspend fun verifyOtp(otp: Int, email: String) {
        _progressLiveData.value = true
        val response = authRepository.verifyOtp(otp, email)
        _isOtpVerified.value = response.status
        _progressLiveData.value = false
    }

    suspend fun sendOtp(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
    ): Boolean {
        if (firstName.isBlank()) {
            _errorLiveData.value = "First name cannot be blank"
        } else if (lastName.isBlank()) {
            _errorLiveData.value = "Last name cannot be blank"
        } else if (email.isBlank()) {
            _errorLiveData.value = "Email is empty or invalid"
        } else if (phone.isBlank()) {
            _errorLiveData.value = "Phone number cannot be blank"
        }else {
            _progressLiveData.value = true
            val signUpModel = SignUpModel("$firstName $lastName", phone, email, "", "", "")
            val response = authRepository.sendOtp(email)
            _signUpModelLiveData.value = signUpModel
            _progressLiveData.value = false

            countDownTimer(1)
            return response?.status!!
        }
        return false
    }

    private fun countDownTimer(duration: Int) { //A function that creates a countdown timer for OTP value expiration
        val initialTime = duration * 60
        var remainingTime = initialTime

        timer(period = 1000) {
            if (remainingTime > 0) {
                val formattedTime = formatTime(remainingTime)
                _otpTimer.postValue(formattedTime)
                remainingTime--
            } else if (remainingTime == 0) {
                _shouldResendOtp.postValue(true)
                _otpTimer.postValue("Time Elapsed!")
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

    suspend fun getUserDetails() {
        _userLiveData.value = dataStoreManager.readUserData()!!
    }

    suspend fun fetchMemberDetailsByEmail(memberEmail: String?): Member? {
        val member = authRepository.getMemberDetails(memberEmail!!)
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

    fun resendOtp(email: String) {
        TODO("Not yet implemented")
    }


}