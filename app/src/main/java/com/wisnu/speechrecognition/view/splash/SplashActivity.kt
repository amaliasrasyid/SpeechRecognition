package com.wisnu.speechrecognition.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_ADMIN
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_GURU
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_SISWA
import com.wisnu.speechrecognition.utils.UtilsCode.TIME_DELAY_SCREEN
import com.wisnu.speechrecognition.view.main.ui.student.MainActivity
import com.wisnu.speechrecognition.view.auth.AuthActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.TeacherActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_splash)

        activityScope.launch {
            delay(TIME_DELAY_SCREEN)

            // check session auth
            val userPreference = UserPreference(this@SplashActivity)
            if (userPreference.getLogin()) {
                if(userPreference.getUser().role == ROLE_ADMIN){
                    startActivity(Intent(this@SplashActivity, TeacherActivity::class.java))
                    finish()
                }else if(userPreference.getUser().role == ROLE_SISWA){
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                finish()
            }
        }
    }
}