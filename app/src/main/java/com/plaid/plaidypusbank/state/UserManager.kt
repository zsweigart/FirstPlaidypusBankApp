package com.plaid.plaidypusbank.state

import android.util.Log
import androidx.annotation.WorkerThread
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

  var sessionActive: Boolean = false

  @WorkerThread
  fun isLoggedIn() = userStorage.loadUser() != null

  @WorkerThread
  fun loadUser(): User? = userStorage.loadUser()

  suspend fun login(email: String, password: String, pushToken: String): LoginError? {
    try {
      val response = bankApi.login(email, password, pushToken)
      return if (response.isSuccessful) {
        val loginResponse = response.body()
        if (loginResponse != null) {
          sessionActive = true
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
    disableFingerprint()
    userStorage.clearUser()
  }

  @WorkerThread
  fun isFingerprintEnabled() = userStorage.isFingerprintEnabled()

  fun enableFingerprint() {
    userStorage.setFingerprintEnabled()
  }

  fun disableFingerprint() {
    userStorage.setFingerprintDisabled()
  }
}
