package com.plaid.plaidypusbank.home.approve

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plaid.plaidypusbank.models.PendingPushApproval
import com.plaid.plaidypusbank.models.User
import com.plaid.plaidypusbank.state.UserManager
import com.plaid.plaidypusbank.storage.PendingPushStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApproveViewModel @ViewModelInject constructor(
  private val userManager: UserManager,
  private val pendingPushStorage: PendingPushStorage,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  val approveState = MutableLiveData<ApproveState>()
  var user: User? = null
  var fingerprintEnabled: Boolean = false
  var pendingPushApproval: PendingPushApproval? = null

  init {
    viewModelScope.launch {
      user = withContext(Dispatchers.IO) {
        userManager.loadUser()
      }

      fingerprintEnabled = withContext(Dispatchers.IO) {
        userManager.isFingerprintEnabled()
      }
      pendingPushApproval = withContext(Dispatchers.IO) {
        pendingPushStorage.loadPendingPush()
      }

      approveState.value = if (userManager.sessionActive) {
        ApproveState.Authorized(pendingPushApproval)
      } else {
        ApproveState.Unauthorized
      }
    }
  }

  fun clearPending() {
    pendingPushStorage.clear()
  }

  fun clearState() {
    userManager.logout()
    pendingPushStorage.clear()
  }

  fun startSession() {
    userManager.sessionActive = true
  }
}
