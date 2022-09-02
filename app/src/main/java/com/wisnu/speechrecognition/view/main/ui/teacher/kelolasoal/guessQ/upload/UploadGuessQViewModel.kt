package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wisnu.speechrecognition.model.questions.QuestionStudyResponse
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQViewModel

class UploadGuessQViewModel : ViewModel() {
    private var _guessQ = MutableLiveData<QuestionStudyResponse>()
    private val TAG = UploadLessonQViewModel::class.java.simpleName
    private val RESPONSE_CLASS = QuestionStudyResponse::class.java

    fun store():LiveData<QuestionStudyResponse>{
        //TODO: belum ada implementasi
        return _guessQ
    }

}