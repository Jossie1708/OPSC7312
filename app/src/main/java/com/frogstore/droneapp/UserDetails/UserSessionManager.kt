package com.frogstore.droneapp.UserDetails
import android.content.Context
import android.content.SharedPreferences

class UserSessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun login(username: String, email: String): Boolean {
        val isLoggedIn = true

        if (isLoggedIn) {
            saveUserSession(username, email)
            return true
        } else {
            return false
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveUserSession(username: String, email: String) {
        sharedPreferences.edit()
            .putString("username", username)
            .putString("email", email)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun getUserSession(): LoginState? {
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (username != null && email != null && isLoggedIn) {
            return LoginState(loggedInUser  = username, email = email)
        } else {
            return null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
}