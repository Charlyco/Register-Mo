package com.register.app.viewmodel

import android.accounts.AccountAuthenticatorResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.register.app.dto.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class AuthViewModel @Inject constructor(): ViewModel() {
    private val _shouldResendOtp: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldResendOtp: LiveData<Boolean> = _shouldResendOtp
    private val _isOtpVerified: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOtpVerified: LiveData<Boolean> = _isOtpVerified
    private val _otpLiveData: MutableLiveData<String>? = MutableLiveData("")
    val otpTimer: LiveData<String>? = _otpLiveData
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
            //TODO call Repository signup function
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
        delay(3000)
        countDownTimer(3)
        return true
    }

    private fun countDownTimer(duration: Int) { //A function that creates a countdown timer for OTP value expiration
        val initialTime = duration * 60
        var remainingTime = initialTime

        timer(period = 1000) {
            if (remainingTime >= 0) {
                val formattedTime = formatTime(remainingTime)
                _otpLiveData?.postValue(formattedTime)
                remainingTime--
                _shouldResendOtp.postValue(false)
            }else {
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

    fun signIn(email: String, password: String): AuthResponse {
        TODO("Not yet implemented")
    }

}