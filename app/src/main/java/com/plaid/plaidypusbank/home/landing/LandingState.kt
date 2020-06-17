package com.plaid.plaidypusbank.home.landing

import com.plaid.plaidypusbank.state.LoginError

sealed class LandingState {
  object Login : LandingState()
  object Loading : LandingState()
  object LoggedIn : LandingState()
  data class Error(val loginError: LoginError) : LandingState()
}
