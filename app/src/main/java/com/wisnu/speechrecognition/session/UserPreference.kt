package com.wisnu.speechrecognition.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kontakanprojects.apptkslb.local_db.Login
import com.wisnu.speechrecognition.local_db.User

class UserPreference(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_USER_IMAGE = "user_image"

        //        private const val KEY_USER_CONTACT = "user_contact"
        private const val KEY_LOGIN = "user_login"
    }

    fun setUser(values: User) {
        preferences.edit {
            putInt(KEY_USER_ID, values.id ?: 0)
            putString(KEY_USER_NAME, values.nama)
            putString(KEY_USER_EMAIL, values.email)
            putInt(KEY_USER_ROLE, values.role ?: 0)
            putString(KEY_USER_PASSWORD, values.password)
            putString(KEY_USER_IMAGE, values.gambar)
//            putString(KEY_USER_CONTACT, values.numberPhone)
        }
    }

    fun setLogin(value: Login) {
        preferences.edit {
            putBoolean(KEY_LOGIN, value.isLoginValid)
        }
    }

    fun getUser(): User {
        return User(
            id = preferences.getInt(KEY_USER_ID, 0),
            nama = preferences.getString(KEY_USER_NAME, ""),
            email = preferences.getString(KEY_USER_EMAIL, ""),
            role = preferences.getInt(KEY_USER_ROLE, 0),
            password = preferences.getString(KEY_USER_PASSWORD, ""),
            gambar = preferences.getString(KEY_USER_IMAGE, "")
        )
    }

    fun getLogin() = preferences.getBoolean(KEY_LOGIN, false)

    fun removeUser() {
        preferences.edit {
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_ROLE)
            remove(KEY_USER_PASSWORD)
            remove(KEY_USER_IMAGE)
            remove(KEY_LOGIN)
//            remove(KEY_USER_CONTACT)
        }
    }

//    fun removeLogin() {
//        preferences.edit {
//            remove(KEY_LOGIN)
//        }
//    }
}