package com.register.app.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.AuthResponseWrapper
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.LoginUserModel
import com.register.app.dto.SignUpModel
import com.register.app.dto.UpdateUserResponse
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.repository.AuthRepository
import com.register.app.repository.ChatRepository
import com.register.app.util.DataStoreManager
import com.register.app.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.LocalDateTime
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
    private val _shouldResendOtp: MutableLiveData<Boolean> = MutableLiveData()
    val shouldResendOtp: LiveData<Boolean> = _shouldResendOtp
    private val _isOtpVerified: MutableLiveData<Boolean?> = MutableLiveData()
    val isOtpVerified: LiveData<Boolean?> = _isOtpVerified
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
    private val _userProfileImage: MutableLiveData<String?> = MutableLiveData()
    val userProfileImage: LiveData<String?> = _userProfileImage
    private val _userEmail: MutableLiveData<String> = MutableLiveData()
    val userEmail: LiveData<String> = _userEmail

//    init {
//        viewModelScope.launch {
//            _userLiveData.value = dataStoreManager.readUserData()
//        }
//    }

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

    suspend fun verifyOtp(otp: Int, email: String): GenericResponse {
        _progressLiveData.value = true
        val response = authRepository.verifyOtp(otp, Utils.normaliseString(email))
        _isOtpVerified.value = response.status
        _progressLiveData.value = false
        return response
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
            val isTaken = authRepository.checkEmailAndPhone(Utils.normaliseString(email), phone)
            if (isTaken.status) {
                val signUpModel = SignUpModel("$firstName $lastName", phone, Utils.normaliseString(email), "", "", "")
                val response = authRepository.sendOtp(email)
                _signUpModelLiveData.value = signUpModel
                _progressLiveData.value = false
                countDownTimer(2)
                return response?.status!!
            } else {
                _errorLiveData.value = isTaken.message
                _progressLiveData.value = false
            }
        }
        return false
    }

    suspend fun sendOtp(email: String): GenericResponse? {
        _userEmail.value = email
        _progressLiveData.value = true
        val response = authRepository.sendOtp(email)
        _progressLiveData.value = false
        if (response?.status == true) countDownTimer(2)
        return response
    }

    private fun countDownTimer(duration: Int) { //A function that creates a countdown timer for OTP value expiration
        val initialTime = duration * 60
        var remainingTime = initialTime

        timer(period = 1000) {
            remainingTime--
            if (remainingTime > 0) {
                val formattedTime = formatTime(remainingTime)
                _otpTimer.postValue(formattedTime)
            } else {
                _shouldResendOtp.postValue(true)
                _otpTimer.postValue( "Time Elapsed!")
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
           val authResponse = authRepository.login(LoginUserModel(Utils.normaliseString(email), password))
        if (authResponse?.status == true) {
            dataStoreManager.writeUserData(authResponse.data?.member!!)
            dataStoreManager.writeTokenData(authResponse.data.authToken)
            dataStoreManager.writeRefreshTokenData(authResponse.data.refreshToken)
            dataStoreManager.writeLoginTime(LocalDateTime.now())
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
        if (deviceId != null && firebaseToken != null) {
            val shouldUpdate = chatRepository.checkTokenWithDeviceId(deviceId, firebaseToken).data
            if (shouldUpdate == true) {
                chatRepository.updateFcmToken(FirebaseTokenModel(deviceId, firebaseToken))
            }
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

    suspend fun getMemberDetails(email: String): Member? {
        _progressLiveData.value = true
        Log.d("Member", "fetching member details")
        val member = authRepository.getMemberDetails(Utils.normaliseString(email))
        _intendingMemberLiveData.value = member
        _progressLiveData.value = false
        return member
    }

    suspend fun resendOtp(email: String) {
        _progressLiveData.value = true
        authRepository.sendOtp(Utils.normaliseString(email))
        _progressLiveData.value = false
        countDownTimer(2)
    }

    suspend fun uploadProfilePic(inputStream: InputStream, mimeType: String?, fileNameFromUri: String?): ImageUploadResponse {
        val requestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
        val response = authRepository.uploadImage(requestBody, fileNameFromUri!!)
        _progressLiveData.value = true
        _userProfileImage.value = response.data?.secureUrl
        _progressLiveData.value = false
        return response
    }

    suspend fun uploadCroppedProfilePic(bitmap: Bitmap, mimeType: String?, fileNameFromUri: String?): ImageUploadResponse {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val requestBody = byteArray.toRequestBody(mimeType?.toMediaTypeOrNull())
        val response = authRepository.uploadImage(requestBody, fileNameFromUri!!)
        _progressLiveData.value = true
        _userProfileImage.value = response.data?.secureUrl
        _progressLiveData.value = false

        return response
    }

    suspend fun updateUserData(user: Member?): UpdateUserResponse {
        _progressLiveData.value = true
        val response = authRepository.updateUserData(user?.userId!!, user)
        if (response.status) {
            _userLiveData.value = response.data
            dataStoreManager.writeUserData(response.data!!)
        }
        _progressLiveData.value = false
        return response
    }

    suspend fun updateUserProfilePic(user: Member?): UpdateUserResponse {
        _progressLiveData.value = true
        val response = authRepository.updateUserData(user?.userId!!, user)
        if (response.status) {
            _userLiveData.value = response.data
            dataStoreManager.writeUserData(response.data!!)
        }
        _progressLiveData.value = false
        return response
    }

    suspend fun shouldLogin(): Boolean {
        if (dataStoreManager.readRefreshToken() != null) {
            val issueDate = LocalDateTime.parse(dataStoreManager.readRefreshToken()?.issueDate)
            val validity = dataStoreManager.readRefreshToken()?.validity
            return LocalDateTime.now() > validity?.div(1000)?.let { issueDate.plusSeconds(it) }
        } else return true
    }

    suspend fun refreshToken() {
        _progressLiveData.value = true
        val refreshToken = dataStoreManager.readRefreshToken()?.refreshToken
        Log.d("REFRESH", refreshToken.toString())
        val response = authRepository.getRefreshToken("Bearer ${refreshToken!!}")
        Log.d("REFRESH", response.toString())
        if (response.status) {
            dataStoreManager.writeUserData(response.data?.member!!)
            dataStoreManager.writeTokenData(response.data.authToken)
            dataStoreManager.writeLoginTime(LocalDateTime.now())
        }
        _progressLiveData.value = false
    }

    suspend fun shouldRefreshToken(): Boolean {
        val loginTime = dataStoreManager.readLoginTime()
        return LocalDateTime.now() > loginTime?.plusSeconds(2700) // refresh token evey 45 mins
    }

    suspend fun reloadUserData() {
            _progressLiveData.value = true
            val userData: Member? = authRepository.reloadUserData(dataStoreManager.readUserData()?.emailAddress)
        if (userData != null) {
            dataStoreManager.writeUserData(userData)
        }
            _progressLiveData.value = false
    }

    suspend fun resetPassword(password: String): GenericResponse {
        val email = userEmail.value
        _progressLiveData.value = true
        val response = authRepository.resetPassword(email, password)
        _progressLiveData.value = false
        return response
    }

    fun clearOtpVerificationLiveData(value: Boolean) {
        _isOtpVerified.value = null
    }
}