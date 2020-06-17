package com.plaid.plaidypusbank.state

import android.util.Log
import com.plaid.plaidypusbank.models.User
import com.plaid.plaidypusbank.networking.BankApi
import com.plaid.plaidypusbank.storage.UserStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(private val bankApi: BankApi, private val userStorage: UserStorage) {

  init {
    userStorage.loadUser()
  }

  suspend fun isLoggedIn() = userStorage.loadUser() != null

  suspend fun login(email: String, password: String, pushToken: String): LoginError? {
    try {
      val response = bankApi.login(email, password, pushToken)
      return if (response.isSuccessful) {
        val loginResponse = response.body()
        if (loginResponse != null) {
          userStorage.saveUser(User(loginResponse.userSessionToken))
          Log.i("ZDS", loginResponse.userSessionToken)
          null
        } else {
          LoginError()
        }
      } else {
        return LoginError()
      }
    } catch (e: Exception) {
      return LoginError()
    }
  }

  fun logout() {
    userStorage.clearUser()
  }
}
