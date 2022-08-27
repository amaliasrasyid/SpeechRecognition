package com.wisnu.speechrecognition.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.wisnu.speechrecognition.db.User

internal class UserPreference(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_FULLNAME = "user_fullname"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_USER_PROFILE_IMAGE = "user_profile_image"

        //        private const val KEY_USER_CONTACT = "user_contact"
        private const val KEY_LOGIN = "user_login"
    }

    fun setUser(values: User) {
        preferences.edit {
            putInt(KEY_USER_ID, values.id ?: 0)
            putString(KEY_USER_FULLNAME, values.name)
            putInt(KEY_USER_ROLE, values.role ?: 0)
            putString(KEY_USER_PASSWORD, values.password)
//            putString(KEY_USER_CONTACT, values.numberPhone)
        }
    }

    fun setLogin(value: Boolean) {
        preferences.edit {
            putBoolean(KEY_LOGIN, value)
        }
    }

    fun getUser(): User {
        return User(
            id = preferences.getInt(KEY_USER_ID, 0),
            name = preferences.getString(KEY_USER_FULLNAME, ""),
            role = preferences.getInt(KEY_USER_ROLE, 0),
            password = preferences.getString(KEY_USER_PASSWORD, "")
//            numberPhone = preferences.getString(KEY_USER_CONTACT, ""),
        )
    }

    fun getLogin() = preferences.getBoolean(KEY_LOGIN, false)

    fun removeUser() {
        preferences.edit {
            remove(KEY_USER_ID)
            remove(KEY_USER_FULLNAME)
            remove(KEY_USER_ROLE)
            remove(KEY_USER_PASSWORD)
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