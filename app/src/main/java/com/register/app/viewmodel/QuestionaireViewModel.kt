package com.register.app.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.register.app.dto.AllResponsesWrapper
import com.register.app.dto.CreateFormModel
import com.register.app.dto.FormUserResponseDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.QuestionnaireData
import com.register.app.dto.QuestionnaireEntry
import com.register.app.dto.QuestionnaireResponse
import com.register.app.enums.FormStatus
import com.register.app.model.Group
import com.register.app.repository.QuestionnaireRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val questionnaireRepository: QuestionnaireRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _questionnaireEntries: MutableLiveData<List<QuestionnaireEntry>?> = MutableLiveData()
    val questionnaireEntries: LiveData<List<QuestionnaireEntry>?> = _questionnaireEntries
    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> = _loadingState
    private val _groupQuestionnaires: MutableLiveData<List<QuestionnaireData>?> = MutableLiveData()
    val groupQuestionnaires: LiveData<List<QuestionnaireData>?> = _groupQuestionnaires
    private val _downloadProgress: MutableLiveData<Int> = MutableLiveData(0)
    val downloadProgress: LiveData<Int> = _downloadProgress
    private val _questionnaireResponseList: MutableLiveData<MutableList<FormUserResponseDto>?> = MutableLiveData()
    val questionnaireResponseList: LiveData<MutableList<FormUserResponseDto>?> = _questionnaireResponseList

    fun addQuestion(question: QuestionnaireEntry) {
        if (_questionnaireEntries.value == null) {
            _questionnaireEntries.value = mutableListOf(question)
        }else {
            val tempList = questionnaireEntries.value?.toMutableList()
            tempList?.add(question)
            _questionnaireEntries.value = tempList
        }
    }

    fun deleteQuestionnaireEntry(question: QuestionnaireEntry) {
        val tempList = questionnaireEntries.value?.toMutableList()
        tempList?.remove(question)
        _questionnaireEntries.value = tempList
    }

    suspend fun postQuestionnaire(title: String, group: Group?): GenericResponse? {
        val questionnaireModel = CreateFormModel(
            title,
            group!!.groupName,
            group.groupId,
            LocalDateTime.now().toString(),
            questionnaireEntries.value!!)
        _loadingState.value = true
        val response = questionnaireRepository.postQuestionnaire(questionnaireModel)
        _loadingState.value = false
        return response
    }

    suspend fun getQuestionnaires(groupId: Int) {
        _loadingState.value = true
        val response = questionnaireRepository.getQuestionnaires(groupId)
        _groupQuestionnaires.value = response.data
        _loadingState.value = false
    }

    suspend fun submitQuestionnaireResponse(
        userResponses: MutableList<QuestionnaireResponse>,
        questionnaireData: QuestionnaireData,
        membershipId: String?
    ): GenericResponse? {
        val userResponseModel = FormUserResponseDto(
            null,
            dataStoreManager.readUserData()?.fullName,
            membershipId,
            LocalDateTime.now().toString(),
            questionnaireData.formId,
            questionnaireData.title,
            questionnaireData.groupId,
            userResponses
        )
        _loadingState.value = true
        val response = questionnaireRepository.submitQuestionnaireResponse(userResponseModel)
        _loadingState.value = false
        return response

    }

    suspend fun deleteQuestionnaire(questionnaireData: QuestionnaireData): GenericResponse {
        _loadingState.value = true
        val response = questionnaireRepository.deleteQuestionnaire(questionnaireData.formId!!)
        _loadingState.value = false
        return response
    }

    suspend fun endQuestionnaire(questionnaireData: QuestionnaireData): GenericResponse {
        _loadingState.value = true
        val response = questionnaireRepository.endQuestionnaire(questionnaireData.formId!!, FormStatus.COMPLETED.name)
        _loadingState.value = false
        return response
    }

    suspend fun downloadAllResponses(responses: List<FormUserResponseDto>?, context: Context) {
        var downloadProgress = 0
        try {
            responses?.forEach { userResponse ->
                val response = questionnaireRepository.downloadResponse(userResponse.formId!!, userResponse.responseId!!)
                if (response != null) {
                    val pdfBytes = response.byteStream()
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "report${LocalDateTime.now()}.pdf")
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                    }
                    val uri = context.contentResolver.insert(
                        MediaStore.Files.getContentUri("external"),
                        contentValues
                    )
                    val outputStream = context.contentResolver.openOutputStream(uri!!)
                    val buffer = ByteArray(4096)
                    var read: Int
                    withContext(Dispatchers.IO) {
                        while (pdfBytes.read(buffer).also { read = it } != -1) {
                            outputStream!!.write(buffer, 0, read)
                        }
                        outputStream!!.flush()
                        outputStream.close()
                    }
                    if (downloadProgress < responses.size) {  // track download progress
                        downloadProgress++
                        _downloadProgress.value = downloadProgress
                    }
                }
            }
        }catch (e: Exception) {
            //
        }
    }

    suspend fun getUserResponses(questionnaireData: QuestionnaireData): AllResponsesWrapper? {
        _loadingState.value = true
        val response = questionnaireRepository.getUserResponses(questionnaireData.formId!!)
        _loadingState.value = false
        _questionnaireResponseList.value = response?.data?.toMutableList()
        return response
    }

}