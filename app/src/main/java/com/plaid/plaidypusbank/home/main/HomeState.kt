package com.plaid.plaidypusbank.home.main

sealed class HomeState {
  object PendingApproval : HomeState()
  object Default : HomeState()
}
