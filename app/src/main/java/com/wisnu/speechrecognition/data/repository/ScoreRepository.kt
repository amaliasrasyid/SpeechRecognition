package com.wisnu.speechrecognition.data.repository

import com.wisnu.speechrecognition.data.source.ScoreDataSource
import com.wisnu.speechrecognition.data.source.remote.ScoreRemoteDataSource
import javax.inject.Inject

class ScoreRepository @Inject constructor(private val dataSource: ScoreRemoteDataSource): ScoreDataSource {
}