package com.wisnu.speechrecognition.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_GURU
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
//        window.requestFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_splash)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            @Suppress("DEPRECATION")
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }

        activityScope.launch {
            delay(TIME_DELAY_SCREEN)

            // check session auth
            val userPreference = UserPreference(this@SplashActivity)
            if (userPreference.getLogin()) {
                if(userPreference.getUser().role == ROLE_GURU){
                    startActivity(Intent(this@SplashActivity, TeacherActivity::class.java))
                    finish()
                }else{
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