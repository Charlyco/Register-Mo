package com.register.app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(): ViewModel() {
    private val _errorLiveData: MutableLiveData<String?> = MutableLiveData("")
    val errorLiveData: LiveData<String?> = _errorLiveData

    fun submitRequest(
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

}