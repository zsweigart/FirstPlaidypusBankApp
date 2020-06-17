package com.plaid.plaidypusbank.home.landing

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.plaid.plaidypusbank.PlaidypusApplication
import com.plaid.plaidypusbank.models.User
import com.plaid.plaidypusbank.state.LoginError
import com.plaid.plaidypusbank.state.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingViewModel @ViewModelInject constructor(
  private val userManager: UserManager,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  val landingState = MutableLiveData<LandingState>()
  var user: User? = null
  var fingerprintEnabled: Boolean = false

  init {
    landingState.value = LandingState.Loading
  }

  fun initialize() {
    viewModelScope.launch {
      user = withContext(Dispatchers.IO) {
        userManager.loadUser()
      }

      fingerprintEnabled = withContext(Dispatchers.IO) {
        userManager.isFingerprintEnabled()
      }

      landingState.value = LandingState.Login(user)
    }
  }

  fun login(email: String, password: String) {
    landingState.value = LandingState.Loading
    FirebaseInstanceId.getInstance().instanceId
      .addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
          Log.w(PlaidypusApplication.TAG, "getInstanceId failed", task.exception)
          return@OnCompleteListener
        }

        // Get new Instance ID token
        val token = task.result!!.token

        // Log
        Log.d(PlaidypusApplication.TAG, token)

        viewModelScope.launch {
          val loginError = withContext(Dispatchers.IO) {
            userManager.login(email, password, token)
          }

          if (loginError == null) {
            user = withContext(Dispatchers.IO) {
              userManager.loadUser()
            }

            landingState.value = LandingState.LoggedIn(user)
          } else {
            landingState.value = LandingState.Error(user, LoginError())
          }
        }
      })
  }

  fun enableFingerprint() {
    userManager.enableFingerprint()
  }

  fun startSession() {
    userManager.sessionActive = true
  }
}
