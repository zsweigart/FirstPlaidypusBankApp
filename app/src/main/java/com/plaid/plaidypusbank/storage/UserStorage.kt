package com.plaid.plaidypusbank.storage

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.plaid.plaidypusbank.models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStorage @Inject constructor(application: Application, private val gson: Gson) {
  companion object {
    const val SHARED_PREFS_NAME = "USER_PREFS"
    const val USER_KEY = "user"
  }

  private var user: User? = null

  private val sharedPrefs = application.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

  @WorkerThread
  fun loadUser(): User? =
    if (user != null) {
      user
    } else {
      gson.fromJson(sharedPrefs.getString(USER_KEY, ""), User::class.java)
    }

  fun saveUser(user: User) {
    this@UserStorage.user = user
    sharedPrefs.edit().putString(USER_KEY, gson.toJson(user)).apply()
  }

  fun clearUser() {
    user = null
    sharedPrefs.edit().clear().apply()
  }
}
