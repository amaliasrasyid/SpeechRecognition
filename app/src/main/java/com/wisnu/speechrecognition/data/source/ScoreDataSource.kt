package com.wisnu.speechrecognition.data.source

import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ScoreDataSource {
    fun getStudentScores(materyType: Int, studentId: Int): Flow<Resource<StudentScoresResponse>>
    fun getStudentGameScores(gameType: Int, studentId: Int): Flow<Resource<StudentScoreResponse>>
}