package com.plaid.plaidypusbank.networking.models

data class LoginRequest(val email: String, val password: String, val pushToken: String)
