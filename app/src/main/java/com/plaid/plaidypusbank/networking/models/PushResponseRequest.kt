package com.plaid.plaidypusbank.networking.models

data class PushResponseRequest(val userSessionToken: String, val pushId: String, val approved: Boolean)
