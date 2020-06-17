package com.plaid.plaidypusbank.home.landing

import com.plaid.plaidypusbank.models.User
import com.plaid.plaidypusbank.state.LoginError

sealed class LandingState {
  data class Login(val user: User?) : LandingState()
  object Loading : LandingState()
  data class LoggedIn(val user: User?) : LandingState()
  data class Error(val user: User?, val loginError: LoginError) : LandingState()
}
