package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.AuthResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.SignUpModel
import com.register.app.model.Member
import com.register.app.repository.AuthRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
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

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        rePassword: String
    ): Boolean {
        if (firstName.isBlank()) {
            _errorLiveData.value = "First name cannot be blank"
        } else if (lastName.isBlank()) {
            _errorLiveData.value = "last name cannot be blank"
        } else if (email.isBlank()) {
            _errorLiveData.value = "Email is empty or invalid"
        } else if (password.length < 6) {
            _errorLiveData.value = "Password must be at least 6 characters"
        }else if (rePassword != password) {
            _errorLiveData.value = "Password mismatch"
        } else {
            val signUpModel = SignUpModel("$firstName $lastName", email, password, rePassword, phoneNumber.value)
            viewModelScope.launch {
                val authResponse =  authRepository.signUp(signUpModel)
                dataStoreManager.writeAuthData(authResponse?.member?.emailAddress!!)  //to be modified
            }
        }
        return true;
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
                _otpLiveData?.postValue(formattedTime)
                remainingTime--
            } else if (remainingTime == 0) {
                _shouldResendOtp.postValue(true)
                _otpLiveData?.postValue("Time Elapsed!")
                cancel()
            }
        }
    }
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun signIn(email: String, password: String): AuthResponse? {
        var authResponse: AuthResponse? = null
       viewModelScope.launch {
           authResponse = authRepository.login(LoginUserModel(email, password))
           //dataStoreManager.writeTokenData(authResponse?.authToken!!)
           //dataStoreManager.writeUserRoleData(authResponse?.member?.memberPost!!)
       }
        return authResponse
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
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Member",
            0.0, "",
            "USER", listOf())
    }

    fun getUserDetails(): Member {
        return Member(1,
            "Uche Egemba",
            "+2347037590923",
            "charlyco835@gmail.com",
            "12 Achuzilam Streen, Oppsite Divina Hospital, Nekede Owerri", "",
            "ACTIVE",
            "Member",
            0.0, "",
            "USER", listOf())
    }
}