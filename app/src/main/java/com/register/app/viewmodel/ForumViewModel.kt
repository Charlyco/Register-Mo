package com.register.app.viewmodel

import androidx.lifecycle.ViewModel
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager): ViewModel() {

}
