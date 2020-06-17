package com.plaid.plaidypusbank.settings

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plaid.plaidypusbank.state.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel @ViewModelInject constructor(
  private val userManager: UserManager,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  val fingerprintEnabled: MutableLiveData<Boolean> = MutableLiveData()

  init {
    viewModelScope.launch {
      fingerprintEnabled.value = withContext(Dispatchers.IO) {
        userManager.isFingerprintEnabled()
      }
    }
  }

  fun enableFingerprint() {
    userManager.enableFingerprint()
  }

  fun disableFingerprint() {
    userManager.disableFingerprint()
  }
}
