package com.wisnu.speechrecognition.data.repository

import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.data.source.ScoreDataSource
import com.wisnu.speechrecognition.data.source.remote.ScoreRemoteDataSource
import com.wisnu.speechrecognition.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScoreRepository @Inject constructor(private val dataSource: ScoreRemoteDataSource): ScoreDataSource {

    override fun getStudentScores(
        materyType: Int,
        studentId: Int
    ): Flow<Resource<StudentScoresResponse>> =  dataSource.getStudentScores(materyType,studentId)

    override fun getStudentGameScores(
        gameType: Int,
        studentId: Int
    ): Flow<Resource<StudentScoreResponse>> = dataSource.getStudentGameScores(gameType,studentId)

}