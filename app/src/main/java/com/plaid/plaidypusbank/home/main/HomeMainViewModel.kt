package com.plaid.plaidypusbank.home.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plaid.plaidypusbank.storage.PendingPushStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMainViewModel @ViewModelInject constructor(
  private val pendingPushStorage: PendingPushStorage,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  val homeState = MutableLiveData<HomeState>()

  init {
    homeState.value = HomeState.Default
  }

  fun checkForPending() {
    viewModelScope.launch {
      val pendingPushApproval = withContext(Dispatchers.IO) {
        pendingPushStorage.loadPendingPush()
      }
      if (pendingPushApproval != null) {
        homeState.value = HomeState.PendingApproval
      }
    }
  }
}
